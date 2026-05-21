package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import gov.nysenate.ess.core.dao.pec.everfi.EverfiEmployeeMappingDao;
import gov.nysenate.ess.core.model.pec.everfi.EverfiEmployeeMapping;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryRules;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiManagedCategory;
import gov.nysenate.ess.core.service.pec.external.everfi.user.EverfiUser;
import gov.nysenate.ess.core.service.pec.external.everfi.user.EverfiUserClient;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Stage 1 of the sync pipeline: loads desired users, remote users, and the local mapping table.
 * Wraps any failure in {@link EverfiUserSyncLoadException} so the pipeline aborts before planning.
 */
@Service
public class EverfiUserSyncLoader {

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

    public EverfiUserSyncLoader(
            EmployeeInfoService employeeInfoService,
            EverfiUserClient everfiUserClient,
            EverfiEmployeeMappingDao everfiEmployeeMappingDao
    ) {
        this.employeeInfoService = employeeInfoService;
        this.everfiUserClient = everfiUserClient;
        this.everfiEmployeeMappingDao = everfiEmployeeMappingDao;
    }

    /**
     * Loads a set of Users who should be active in Everfi, including their desired category labels
     * (Attended Live, Department, Role) derived from SFMS employee data.
     */
    Set<DesiredUser> loadDesiredUsers() {
        try {
            Set<Employee> employees = employeeInfoService.getAllEmployees(true);
            Set<DesiredUser> result = new HashSet<>();
            for (Employee employee : employees) {
                result.add(DesiredUser.builder()
                        .employeeId(employee.getEmployeeId())
                        .email(employee.getEmail())
                        .firstName(employee.getFirstName())
                        .lastName(employee.getLastName())
                        .desiredLabels(desiredCategoryLabels(employee))
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
    RemoteLoadResult loadRemoteUsers() {
        try {
            Map<String, EverfiEmployeeMapping> mappingsByUuid = everfiEmployeeMappingDao.findAll().stream()
                    .collect(Collectors.toUnmodifiableMap(EverfiEmployeeMapping::everfiUuid, Function.identity()));
            List<EverfiUser> everfiUsers = everfiUserClient.fetchAll();

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

    private List<DesiredLabel> desiredCategoryLabels(Employee emp) {
        List<DesiredLabel> labels = new ArrayList<>();
        labels.add(new DesiredLabel(
                EverfiManagedCategory.ATTENDED_LIVE.categoryName(),
                EverfiCategoryRules.normalizeLabel(EverfiManagedCategory.ATTENDED_LIVE, "No")));
        String department = EverfiCategoryRules.normalizeLabel(
                EverfiManagedCategory.DEPARTMENT, emp.getRespCenterHeadCode());
        if (department != null) {
            labels.add(new DesiredLabel(EverfiManagedCategory.DEPARTMENT.categoryName(), department));
        }
        labels.add(new DesiredLabel(
                EverfiManagedCategory.ROLE.categoryName(),
                EverfiCategoryRules.normalizeLabel(
                        EverfiManagedCategory.ROLE, emp.isSenator() ? "Senator" : "Employee")));
        return labels;
    }
}
