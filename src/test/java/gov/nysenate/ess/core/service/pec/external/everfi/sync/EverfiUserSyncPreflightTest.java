package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import com.google.common.collect.RangeSet;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.dao.pec.everfi.EverfiEmployeeMappingDao;
import gov.nysenate.ess.core.model.pec.everfi.EverfiEmployeeMapping;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.ResponsibilityCenter;
import gov.nysenate.ess.core.model.personnel.ResponsibilityHead;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategory;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryService;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategorySnapshot;
import gov.nysenate.ess.core.service.pec.external.everfi.user.EverfiUser;
import gov.nysenate.ess.core.service.pec.external.everfi.user.EverfiUserClient;
import gov.nysenate.ess.core.service.pec.external.everfi.user.EverfiUserPayloadFactory;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.personnel.EmployeeSearchBuilder;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Category(UnitTest.class)
public class EverfiUserSyncPreflightTest {

    private static TestLoggerControl logControl;

    @BeforeClass
    public static void suppressExpectedWarningLogs() {
        logControl = TestLoggerControl.suppress(EverfiUserSyncPreflight.class);
    }

    @AfterClass
    public static void restoreLoggerLevel() {
        logControl.restore();
    }

    @Test
    public void loadDesiredUsersWrapsLocalLoadFailure() {
        EverfiUserSyncPreflight preflight = new EverfiUserSyncPreflight(
                new FailingEmployeeInfoService(),
                new StubEverfiUserClient(List.of()),
                new StubEverfiEmployeeMappingDao(List.of()),
                null
        );

        assertThatThrownBy(() -> preflight.loadDesiredUsers(emptySnapshot()))
                .isInstanceOf(EverfiUserSyncLoadException.class)
                .hasMessage("Failed to load Desired Users.")
                .hasCauseInstanceOf(IllegalStateException.class);
    }

    @Test
    public void loadRemoteUsersWrapsRemoteLoadFailure() {
        EverfiUserSyncPreflight preflight = new EverfiUserSyncPreflight(
                new StubEmployeeInfoService(Set.of()),
                new FailingEverfiUserClient(),
                new StubEverfiEmployeeMappingDao(List.of()),
                null
        );

        assertThatThrownBy(() -> preflight.loadRemoteUsers(emptySnapshot()))
                .isInstanceOf(EverfiUserSyncLoadException.class)
                .hasMessage("Failed to load Remote Everfi Users.")
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    public void loadRemoteUsersWrapsInvalidRemoteUser() {
        EverfiUserSyncPreflight preflight = new EverfiUserSyncPreflight(
                new StubEmployeeInfoService(Set.of()),
                new StubEverfiUserClient(List.of(everfiUser("everfi-1", null))),
                new StubEverfiEmployeeMappingDao(List.of()),
                null
        );

        assertThatThrownBy(() -> preflight.loadRemoteUsers(emptySnapshot()))
                .isInstanceOf(EverfiUserSyncLoadException.class)
                .hasMessage("Failed to load Remote Everfi Users.")
                .hasCauseInstanceOf(RuntimeException.class);
    }

    @Test
    public void loadRemoteUsersAllowsMissingMapping() {
        EverfiUserSyncPreflight preflight = new EverfiUserSyncPreflight(
                new StubEmployeeInfoService(Set.of()),
                new StubEverfiUserClient(List.of(everfiUser("everfi-1", "user@example.com"))),
                new StubEverfiEmployeeMappingDao(List.of()),
                null
        );

        Set<RemoteUser> remoteUsers = preflight.loadRemoteUsers(emptySnapshot()).remoteUsers();

        RemoteUser remoteUser = assertOne(remoteUsers);
        assertThat(remoteUser.remoteUuid()).isEqualTo("everfi-1");
        assertThat(remoteUser.mapping()).isNull();
    }

    @Test
    public void loadRemoteUsersAssociatesMappingByEverfiUuid() {
        EverfiEmployeeMapping mapping = new EverfiEmployeeMapping(123, "everfi-1");
        EverfiUserSyncPreflight preflight = new EverfiUserSyncPreflight(
                new StubEmployeeInfoService(Set.of()),
                new StubEverfiUserClient(List.of(everfiUser("everfi-1", "user@example.com"))),
                new StubEverfiEmployeeMappingDao(List.of(mapping)),
                null
        );

        Set<RemoteUser> remoteUsers = preflight.loadRemoteUsers(emptySnapshot()).remoteUsers();

        assertThat(assertOne(remoteUsers).mapping()).isEqualTo(mapping);
    }

    @Test
    public void loadRemoteUsers_preservesMissingRemoteEmployeeIdAsNull() {
        EverfiUserSyncPreflight preflight = new EverfiUserSyncPreflight(
                new StubEmployeeInfoService(Set.of()),
                new StubEverfiUserClient(List.of(everfiUser("everfi-1", "user@example.com", null))),
                new StubEverfiEmployeeMappingDao(List.of()),
                null
        );

        Set<RemoteUser> remoteUsers = preflight.loadRemoteUsers(emptySnapshot()).remoteUsers();

        assertThat(assertOne(remoteUsers).remoteEmployeeId()).isNull();
    }

    @Test
    public void loadRemoteUsers_detectsUnmatchedMapping() {
        EverfiEmployeeMapping orphanedMapping = new EverfiEmployeeMapping(123, "everfi-orphan");
        EverfiUserSyncPreflight preflight = new EverfiUserSyncPreflight(
                new StubEmployeeInfoService(Set.of()),
                new StubEverfiUserClient(List.of()),
                new StubEverfiEmployeeMappingDao(List.of(orphanedMapping)),
                null
        );

        EverfiUserSyncPreflight.RemoteLoadResult result = preflight.loadRemoteUsers(emptySnapshot());

        assertThat(result.remoteUsers()).isEmpty();
        assertThat(result.empIdsWithUnmatchedMappings()).containsExactly(123);
    }

    @Test
    public void loadDesiredUsers_populatesCategoryLabels() {
        EverfiCategoryLabel attendLiveLabel = new EverfiCategoryLabel(1, "No");
        EverfiCategoryLabel roleLabel = new EverfiCategoryLabel(2, "Employee");
        EverfiCategorySnapshot snapshot = new EverfiCategorySnapshot(List.of(
                new EverfiCategory(10, EverfiCategorySnapshot.ATTENDED_LIVE, List.of(attendLiveLabel)),
                new EverfiCategory(11, EverfiCategorySnapshot.ROLE, List.of(roleLabel))
        ));

        Employee emp = new Employee();
        emp.setEmployeeId(42);
        emp.setFirstName("Test");
        emp.setLastName("User");
        emp.setEmail("test@nysenate.gov");

        EverfiUserSyncPreflight preflight = new EverfiUserSyncPreflight(
                new StubEmployeeInfoService(Set.of(emp)),
                new StubEverfiUserClient(List.of()),
                new StubEverfiEmployeeMappingDao(List.of()),
                null
        );

        Set<DesiredUser> desiredUsers = preflight.loadDesiredUsers(snapshot);

        DesiredUser desiredUser = assertOne(desiredUsers);
        assertThat(desiredUser.employeeId()).isEqualTo(42);
        assertThat(desiredUser.categoryLabels()).containsExactlyInAnyOrder(attendLiveLabel, roleLabel);
    }

    @Test
    public void ensureTodaysUploadListLabel_returnsExistingLabel() {
        LocalDate today = LocalDate.now();
        EverfiCategoryLabel existing = new EverfiCategoryLabel(
                200, today.format(EverfiCategorySnapshot.UPLOAD_LIST_LABEL_FORMAT));
        EverfiCategorySnapshot snapshot = new EverfiCategorySnapshot(List.of(
                new EverfiCategory(20, EverfiCategorySnapshot.UPLOAD_LIST, List.of(existing))
        ));
        EverfiUserSyncPreflight preflight = new EverfiUserSyncPreflight(
                new StubEmployeeInfoService(Set.of()),
                new StubEverfiUserClient(List.of()),
                new StubEverfiEmployeeMappingDao(List.of()),
                null
        );

        assertThat(preflight.ensureTodaysUploadListLabel(snapshot)).isSameAs(existing);
    }

    @Test
    public void ensureTodaysUploadListLabel_createsWhenMissing() {
        EverfiCategoryLabel created = new EverfiCategoryLabel(201, "Apr 22 2026");
        EverfiCategorySnapshot snapshot = new EverfiCategorySnapshot(List.of(
                new EverfiCategory(20, EverfiCategorySnapshot.UPLOAD_LIST, List.of())
        ));
        EverfiCategoryService categoryService = new StubEverfiCategoryService() {
            @Override
            public EverfiCategoryLabel createLabel(EverfiCategory category, String labelName) {
                return created;
            }
        };
        EverfiUserSyncPreflight preflight = new EverfiUserSyncPreflight(
                new StubEmployeeInfoService(Set.of()),
                new StubEverfiUserClient(List.of()),
                new StubEverfiEmployeeMappingDao(List.of()),
                categoryService
        );

        assertThat(preflight.ensureTodaysUploadListLabel(snapshot)).isSameAs(created);
    }

    @Test
    public void ensureTodaysUploadListLabel_wrapsException() {
        EverfiCategorySnapshot snapshot = new EverfiCategorySnapshot(List.of(
                new EverfiCategory(20, EverfiCategorySnapshot.UPLOAD_LIST, List.of())
        ));
        EverfiCategoryService categoryService = new StubEverfiCategoryService() {
            @Override
            public EverfiCategoryLabel createLabel(EverfiCategory category, String labelName) throws IOException {
                throw new IOException("api error");
            }
        };
        EverfiUserSyncPreflight preflight = new EverfiUserSyncPreflight(
                new StubEmployeeInfoService(Set.of()),
                new StubEverfiUserClient(List.of()),
                new StubEverfiEmployeeMappingDao(List.of()),
                categoryService
        );

        assertThatThrownBy(() -> preflight.ensureTodaysUploadListLabel(snapshot))
                .isInstanceOf(EverfiUserSyncLoadException.class)
                .hasMessage("Failed to ensure today's Upload List label.")
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    public void ensureTodaysUploadListLabel_uploadListCategoryMissing_throwsLoadException() {
        EverfiCategorySnapshot snapshot = emptySnapshot();
        EverfiUserSyncPreflight preflight = new EverfiUserSyncPreflight(
                new StubEmployeeInfoService(Set.of()),
                new StubEverfiUserClient(List.of()),
                new StubEverfiEmployeeMappingDao(List.of()),
                null
        );

        assertThatThrownBy(() -> preflight.ensureTodaysUploadListLabel(snapshot))
                .isInstanceOf(EverfiUserSyncLoadException.class)
                .hasMessage("Failed to ensure today's Upload List label.")
                .hasCauseInstanceOf(IllegalStateException.class);
    }

    @Test
    public void ensureDepartmentLabels_dryRun_doesNotCreateLabels() {
        var categoryService = new RecordingCreateLabelCategoryService();
        EverfiCategorySnapshot snapshot = new EverfiCategorySnapshot(List.of(
                new EverfiCategory(1, EverfiCategorySnapshot.DEPARTMENT, List.of())
        ));
        EverfiUserSyncPreflight preflight = new EverfiUserSyncPreflight(
                new StubEmployeeInfoService(Set.of(employee(1, "ABC"))),
                new StubEverfiUserClient(List.of()),
                new StubEverfiEmployeeMappingDao(List.of()),
                categoryService
        );

        boolean createdAny = preflight.ensureDepartmentLabels(snapshot, true);

        assertThat(createdAny).isFalse();
        assertThat(categoryService.createdLabels).isEmpty();
    }

    @Test
    public void ensureDepartmentLabels_liveRun_createsMissingLabels() {
        var categoryService = new RecordingCreateLabelCategoryService();
        EverfiCategorySnapshot snapshot = new EverfiCategorySnapshot(List.of(
                new EverfiCategory(1, EverfiCategorySnapshot.DEPARTMENT,
                        List.of(new EverfiCategoryLabel(10, "EXISTING")))
        ));
        EverfiUserSyncPreflight preflight = new EverfiUserSyncPreflight(
                new StubEmployeeInfoService(Set.of(employee(1, "EXISTING"), employee(2, "NEW_DEPT"))),
                new StubEverfiUserClient(List.of()),
                new StubEverfiEmployeeMappingDao(List.of()),
                categoryService
        );

        boolean createdAny = preflight.ensureDepartmentLabels(snapshot, false);

        assertThat(createdAny).isTrue();
        assertThat(categoryService.createdLabels).containsExactly("NEW_DEPT");
    }

    @Test
    public void ensureDepartmentLabels_liveRun_noMissingLabels_doesNotCreate() {
        var categoryService = new RecordingCreateLabelCategoryService();
        EverfiCategorySnapshot snapshot = new EverfiCategorySnapshot(List.of(
                new EverfiCategory(1, EverfiCategorySnapshot.DEPARTMENT,
                        List.of(new EverfiCategoryLabel(10, "EXISTING")))
        ));
        EverfiUserSyncPreflight preflight = new EverfiUserSyncPreflight(
                new StubEmployeeInfoService(Set.of(employee(1, "EXISTING"))),
                new StubEverfiUserClient(List.of()),
                new StubEverfiEmployeeMappingDao(List.of()),
                categoryService
        );

        boolean createdAny = preflight.ensureDepartmentLabels(snapshot, false);

        assertThat(createdAny).isFalse();
        assertThat(categoryService.createdLabels).isEmpty();
    }

    @Test
    public void ensureDepartmentLabels_liveRun_filtersNullAndLiteralNullRespCodes() {
        var categoryService = new RecordingCreateLabelCategoryService();
        EverfiCategorySnapshot snapshot = new EverfiCategorySnapshot(List.of(
                new EverfiCategory(1, EverfiCategorySnapshot.DEPARTMENT, List.of())
        ));
        EverfiUserSyncPreflight preflight = new EverfiUserSyncPreflight(
                new StubEmployeeInfoService(Set.of(
                        employee(1, null),
                        employee(2, "null"),
                        employee(3, "VALID")
                )),
                new StubEverfiUserClient(List.of()),
                new StubEverfiEmployeeMappingDao(List.of()),
                categoryService
        );

        preflight.ensureDepartmentLabels(snapshot, false);

        assertThat(categoryService.createdLabels).containsExactly("VALID");
    }

    @Test
    public void ensureDepartmentLabels_departmentCategoryMissing_throwsLoadException() {
        EverfiUserSyncPreflight preflight = new EverfiUserSyncPreflight(
                new StubEmployeeInfoService(Set.of(employee(1, "ABC"))),
                new StubEverfiUserClient(List.of()),
                new StubEverfiEmployeeMappingDao(List.of()),
                null
        );

        assertThatThrownBy(() -> preflight.ensureDepartmentLabels(emptySnapshot(), false))
                .isInstanceOf(EverfiUserSyncLoadException.class)
                .hasMessage("Failed to ensure Department category labels.");
    }

    @Test
    public void ensureDepartmentLabels_wrapsCreateLabelException() {
        EverfiCategorySnapshot snapshot = new EverfiCategorySnapshot(List.of(
                new EverfiCategory(1, EverfiCategorySnapshot.DEPARTMENT, List.of())
        ));
        EverfiCategoryService categoryService = new StubEverfiCategoryService() {
            @Override
            public EverfiCategoryLabel createLabel(EverfiCategory category, String labelName) throws IOException {
                throw new IOException("api error");
            }
        };
        EverfiUserSyncPreflight preflight = new EverfiUserSyncPreflight(
                new StubEmployeeInfoService(Set.of(employee(1, "ABC"))),
                new StubEverfiUserClient(List.of()),
                new StubEverfiEmployeeMappingDao(List.of()),
                categoryService
        );

        assertThatThrownBy(() -> preflight.ensureDepartmentLabels(snapshot, false))
                .isInstanceOf(EverfiUserSyncLoadException.class)
                .hasMessage("Failed to ensure Department category labels.")
                .hasCauseInstanceOf(IOException.class);
    }

    private static EverfiCategorySnapshot emptySnapshot() {
        return new EverfiCategorySnapshot(List.of());
    }

    private static Employee employee(int id, String respCenterHeadCode) {
        Employee emp = new Employee();
        emp.setEmployeeId(id);
        if (respCenterHeadCode != null) {
            ResponsibilityHead head = new ResponsibilityHead();
            head.setCode(respCenterHeadCode);
            ResponsibilityCenter center = new ResponsibilityCenter();
            center.setHead(head);
            emp.setRespCenter(center);
        }
        return emp;
    }

    private static <T> T assertOne(Set<T> set) {
        assertThat(set).hasSize(1);
        return set.iterator().next();
    }

    private static EverfiUser everfiUser(String uuid, String email) {
        return everfiUser(uuid, email, 123);
    }

    private static EverfiUser everfiUser(String uuid, String email, Integer employeeId) {
        return new TestEverfiUser(uuid, email, employeeId);
    }

    private static class StubEmployeeInfoService implements EmployeeInfoService {
        private final Set<Employee> employees;

        private StubEmployeeInfoService(Set<Employee> employees) {
            this.employees = employees;
        }

        @Override
        public Employee getEmployee(int empId) {
            throw unsupported();
        }

        @Override
        public Employee getEmployee(int empId, LocalDate effectiveDate) {
            throw unsupported();
        }

        @Override
        public RangeSet<LocalDate> getEmployeeActiveDatesService(int empId) {
            throw unsupported();
        }

        @Override
        public LocalDate getEmployeesMostRecentContinuousServiceDate(int empId) {
            throw unsupported();
        }

        @Override
        public List<Integer> getEmployeeActiveYearsService(int empId, boolean fiscalYears) {
            throw unsupported();
        }

        @Override
        public Set<Integer> getActiveEmpIds() {
            throw unsupported();
        }

        @Override
        public Set<Employee> getAllEmployees(boolean activeOnly) {
            return employees;
        }

        @Override
        public PaginatedList<Employee> searchEmployees(String term, boolean activeOnly, LimitOffset limitOffset) {
            throw unsupported();
        }

        @Override
        public PaginatedList<Employee> searchEmployees(EmployeeSearchBuilder employeeSearchBuilder,
                                                       LimitOffset limitOffset) {
            throw unsupported();
        }
    }

    private static class FailingEmployeeInfoService extends StubEmployeeInfoService {
        private FailingEmployeeInfoService() {
            super(Set.of());
        }

        @Override
        public Set<Employee> getAllEmployees(boolean activeOnly) {
            throw new IllegalStateException("local load failed");
        }
    }

    private static class StubEverfiUserClient extends EverfiUserClient {
        private final List<EverfiUser> users;

        private StubEverfiUserClient(List<EverfiUser> users) {
            super(null, new EverfiUserPayloadFactory());
            this.users = users;
        }

        @Override
        public List<EverfiUser> fetchAll(EverfiCategorySnapshot snapshot) {
            return users;
        }
    }

    private static class FailingEverfiUserClient extends EverfiUserClient {
        private FailingEverfiUserClient() {
            super(null, new EverfiUserPayloadFactory());
        }

        @Override
        public List<EverfiUser> fetchAll(EverfiCategorySnapshot snapshot) throws IOException {
            throw new IOException("remote load failed");
        }
    }

    private static class TestEverfiUser extends EverfiUser {
        private final String uuid;
        private final String email;
        private final Integer employeeId;

        private TestEverfiUser(String uuid, String email, Integer employeeId) {
            this.uuid = uuid;
            this.email = email;
            this.employeeId = employeeId;
        }

        @Override
        public String getUuid() {
            return uuid;
        }

        @Override
        public Integer getEmployeeId() {
            return employeeId;
        }

        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public String getEmail() {
            return email;
        }

        @Override
        public String getFirstName() {
            return "Test";
        }

        @Override
        public String getLastName() {
            return "User";
        }
    }

    private static class StubEverfiEmployeeMappingDao implements EverfiEmployeeMappingDao {
        private final List<EverfiEmployeeMapping> mappings;

        private StubEverfiEmployeeMappingDao(List<EverfiEmployeeMapping> mappings) {
            this.mappings = mappings;
        }

        @Override
        public List<EverfiEmployeeMapping> findAll() {
            return mappings;
        }

        @Override
        public Optional<EverfiEmployeeMapping> findByEmpId(int empId) {
            throw unsupported();
        }

        @Override
        public Optional<EverfiEmployeeMapping> findByEverfiUuid(String everfiUuid) {
            throw unsupported();
        }

        @Override
        public int insert(EverfiEmployeeMapping mapping) {
            throw unsupported();
        }
    }

    private static class StubEverfiCategoryService extends EverfiCategoryService {
        private StubEverfiCategoryService() {
            super(null);
        }
    }

    private static class RecordingCreateLabelCategoryService extends StubEverfiCategoryService {
        final List<String> createdLabels = new ArrayList<>();

        @Override
        public EverfiCategoryLabel createLabel(EverfiCategory category, String labelName) {
            createdLabels.add(labelName);
            return new EverfiCategoryLabel(99, labelName);
        }
    }

    private static UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException("Not needed for this test.");
    }
}
