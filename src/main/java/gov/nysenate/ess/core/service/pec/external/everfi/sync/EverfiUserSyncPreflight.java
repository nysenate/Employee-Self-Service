package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import gov.nysenate.ess.core.dao.pec.everfi.EverfiEmployeeMappingDao;
import gov.nysenate.ess.core.model.pec.everfi.EverfiEmployeeMapping;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategory;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryService;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategorySnapshot;
import gov.nysenate.ess.core.service.pec.external.everfi.user.EverfiUser;
import gov.nysenate.ess.core.service.pec.external.everfi.user.EverfiUserClient;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Stage 1 of the sync pipeline: ensures remote preconditions (Department labels, today's Upload List
 * label) and loads desired users, remote users, and the local mapping table.
 * Wraps any failure in {@link EverfiUserSyncLoadException} so the pipeline aborts before planning.
 */
@Service
public class EverfiUserSyncPreflight {

    private static final Logger logger = LoggerFactory.getLogger(EverfiUserSyncPreflight.class);

    /**
     * @param remoteUsers                 every user currently in the Everfi snapshot, enriched with mapping if present
     * @param empIdsWithUnmatchedMappings employee IDs whose mapping row points at a UUID no longer in Everfi —
     *                                    must not be routed to CREATE (would duplicate the mapping)
     */
    record RemoteLoadResult(Set<RemoteUser> remoteUsers, Set<Integer> empIdsWithUnmatchedMappings) {
    }

    private final EmployeeInfoService employeeInfoService;
    private final EverfiUserClient everfiUserClient;
    private final EverfiEmployeeMappingDao everfiEmployeeMappingDao;
    private final EverfiCategoryService categoryService;

    public EverfiUserSyncPreflight(
            EmployeeInfoService employeeInfoService,
            EverfiUserClient everfiUserClient,
            EverfiEmployeeMappingDao everfiEmployeeMappingDao,
            EverfiCategoryService categoryService) {
        this.employeeInfoService = employeeInfoService;
        this.everfiUserClient = everfiUserClient;
        this.everfiEmployeeMappingDao = everfiEmployeeMappingDao;
        this.categoryService = categoryService;
    }

    /**
     * Fetch a fresh {@link EverfiCategorySnapshot}. Callers pass the result into the other
     * load methods so a single run sees a consistent view of the categories and avoids
     * repeated remote fetches.
     */
    EverfiCategorySnapshot loadCategorySnapshot() {
        try {
            return categoryService.fetchSnapshot();
        } catch (IOException | RuntimeException ex) {
            throw new EverfiUserSyncLoadException("Failed to load Everfi category snapshot.", ex);
        }
    }

    /**
     * Ensures every employee's {@code respCenterHeadCode} has a corresponding Department label in Everfi,
     * creating any that are missing. Skipped on dry runs — no remote writes occur.
     * Throws {@link EverfiUserSyncLoadException} on any failure so the pipeline aborts before planning.
     *
     * @return true if any new labels were created (snapshot now stale); false otherwise.
     */
    boolean ensureDepartmentLabels(EverfiCategorySnapshot snapshot, boolean dryRun) {
        if (dryRun) return false;
        try {
            EverfiCategory departmentCategory = snapshot.getCategory(EverfiCategorySnapshot.DEPARTMENT);
            if (departmentCategory == null) {
                throw new IllegalStateException("\"Department\" category not found in Everfi.");
            }
            Set<String> existingLabelNames = departmentCategory.getLabels().stream()
                    .map(EverfiCategoryLabel::getLabelName)
                    .collect(Collectors.toSet());

            Set<Employee> employees = employeeInfoService.getAllEmployees(true);
            List<String> missingCodes = employees.stream()
                    .map(Employee::getRespCenterHeadCode)
                    .filter(code -> code != null && !code.equals("null"))
                    .distinct()
                    .filter(code -> !existingLabelNames.contains(code))
                    .toList();

            for (String code : missingCodes) {
                categoryService.createLabel(departmentCategory, code);
            }
            return !missingCodes.isEmpty();
        } catch (IOException | RuntimeException ex) {
            throw new EverfiUserSyncLoadException("Failed to ensure Department category labels.", ex);
        }
    }

    /**
     * Loads a set of Users who should be active in Everfi, including their category labels
     * (Attended Live, Department, Role) derived from employee data.
     */
    Set<DesiredUser> loadDesiredUsers(EverfiCategorySnapshot snapshot) {
        try {
            Set<Employee> employees = employeeInfoService.getAllEmployees(true);
            Set<DesiredUser> result = new HashSet<>();
            for (Employee employee : employees) {
                result.add(DesiredUser.builder()
                        .employeeId(employee.getEmployeeId())
                        .email(employee.getEmail())
                        .firstName(employee.getFirstName())
                        .lastName(employee.getLastName())
                        .categoryLabels(resolveDesiredCategoryLabels(employee, snapshot))
                        .build());
            }
            return Collections.unmodifiableSet(result);
        } catch (RuntimeException ex) {
            throw new EverfiUserSyncLoadException("Failed to load Desired Users.", ex);
        }
    }

    /**
     * Load the current representation of remote Everfi users, with normalized category labels.
     * Also detects local mapping rows whose UUID is absent from the remote snapshot.
     */
    RemoteLoadResult loadRemoteUsers(EverfiCategorySnapshot snapshot) {
        try {
            Map<String, EverfiEmployeeMapping> mappingsByUuid = everfiEmployeeMappingDao.findAll().stream()
                    .collect(Collectors.toUnmodifiableMap(EverfiEmployeeMapping::everfiUuid, Function.identity()));
            List<EverfiUser> everfiUsers = everfiUserClient.fetchAll(snapshot);

            Set<String> fetchedUuids = everfiUsers.stream()
                    .map(EverfiUser::getUuid)
                    .collect(Collectors.toSet());
            Set<Integer> unmatchedMappingEmpIds = mappingsByUuid.values().stream()
                    .filter(m -> !fetchedUuids.contains(m.everfiUuid()))
                    .map(EverfiEmployeeMapping::employeeId)
                    .collect(Collectors.toUnmodifiableSet());

            Set<RemoteUser> result = new HashSet<>();
            for (EverfiUser user : everfiUsers) {
                result.add(RemoteUser.builder()
                        .mapping(mappingsByUuid.get(user.getUuid()))
                        .remoteUuid(user.getUuid())
                        .remoteEmployeeId(user.getEmployeeId())
                        .remoteActive(user.isActive())
                        .remoteFirstName(user.getFirstName())
                        .remoteLastName(user.getLastName())
                        .remoteEmail(user.getEmail())
                        .categoryLabels(user.getCategoryLabels())
                        .build());
            }
            return new RemoteLoadResult(Collections.unmodifiableSet(result), unmatchedMappingEmpIds);
        } catch (IOException | RuntimeException ex) {
            throw new EverfiUserSyncLoadException("Failed to load Remote Everfi Users.", ex);
        }
    }

    /**
     * Finds today's Upload List label, creating it if it does not yet exist. Each live run that
     * creates users tags them with this date-stamped label so admins can later identify the cohort.
     * Must only be called on a live run with at least one CREATE — dry runs must not create labels
     * as a side effect.
     */
    EverfiCategoryLabel ensureTodaysUploadListLabel(EverfiCategorySnapshot snapshot) {
        LocalDate now = LocalDate.now();
        try {
            EverfiCategoryLabel label = snapshot.getUploadListLabel(now);
            if (label != null) {
                return label;
            }
            EverfiCategory uploadListCategory = snapshot.getCategory(EverfiCategorySnapshot.UPLOAD_LIST);
            if (uploadListCategory == null) {
                throw new IllegalStateException("\"Upload List\" category not found in Everfi.");
            }
            return categoryService.createLabel(
                    uploadListCategory, now.format(EverfiCategorySnapshot.UPLOAD_LIST_LABEL_FORMAT));
        } catch (IOException | RuntimeException ex) {
            throw new EverfiUserSyncLoadException("Failed to ensure today's Upload List label.", ex);
        }
    }

    private List<EverfiCategoryLabel> resolveDesiredCategoryLabels(Employee emp, EverfiCategorySnapshot snapshot) {
        EverfiCategoryLabel attendLiveLabel = snapshot.getAttendLiveLabel(emp);
        EverfiCategoryLabel departmentLabel = snapshot.getDepartmentLabel(emp);
        EverfiCategoryLabel roleLabel = snapshot.getRoleLabel(emp);

        logMissingManagedLabel(emp, EverfiCategorySnapshot.ATTENDED_LIVE, attendLiveLabel, null);
        logMissingManagedLabel(emp, EverfiCategorySnapshot.DEPARTMENT, departmentLabel, emp.getRespCenterHeadCode());
        logMissingManagedLabel(emp, EverfiCategorySnapshot.ROLE, roleLabel, emp.isSenator() ? "Senator" : "Employee");

        return Stream.of(
                attendLiveLabel,
                departmentLabel,
                roleLabel
        ).filter(Objects::nonNull).toList();
    }

    private void logMissingManagedLabel(Employee emp, String categoryName,
                                        EverfiCategoryLabel label, String expectedLabelName) {
        if (label == null) {
            logger.warn("Missing Everfi managed label for employee {} in category '{}'{}",
                    emp.getEmployeeId(),
                    categoryName,
                    expectedLabelName != null ? " (expected label: '" + expectedLabelName + "')" : "");
        }
    }
}
