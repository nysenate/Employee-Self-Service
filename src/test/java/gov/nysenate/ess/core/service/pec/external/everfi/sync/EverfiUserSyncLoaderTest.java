package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import com.google.common.collect.RangeSet;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.dao.pec.everfi.EverfiEmployeeMappingDao;
import gov.nysenate.ess.core.model.pec.everfi.EverfiEmployeeMapping;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.ResponsibilityCenter;
import gov.nysenate.ess.core.model.personnel.ResponsibilityHead;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryService;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiManagedCategory;
import gov.nysenate.ess.core.service.pec.external.everfi.user.EverfiUser;
import gov.nysenate.ess.core.service.pec.external.everfi.user.EverfiUserClient;
import gov.nysenate.ess.core.service.pec.external.everfi.user.EverfiUserPayloadFactory;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.personnel.EmployeeSearchBuilder;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Category(UnitTest.class)
public class EverfiUserSyncLoaderTest {

    @Test
    public void loadDesiredUsersWrapsLocalLoadFailure() {
        EverfiUserSyncLoader preflight = new EverfiUserSyncLoader(
                new FailingEmployeeInfoService(),
                new StubEverfiUserClient(List.of()),
                new StubEverfiEmployeeMappingDao(List.of())
        );

        assertThatThrownBy(() -> preflight.loadDesiredUsers())
                .isInstanceOf(EverfiUserSyncLoadException.class)
                .hasMessage("Failed to load Desired Users.")
                .hasCauseInstanceOf(IllegalStateException.class);
    }

    @Test
    public void loadRemoteUsersWrapsRemoteLoadFailure() {
        EverfiUserSyncLoader preflight = new EverfiUserSyncLoader(
                new StubEmployeeInfoService(Set.of()),
                new FailingEverfiUserClient(),
                new StubEverfiEmployeeMappingDao(List.of())
        );

        assertThatThrownBy(preflight::loadRemoteUsers)
                .isInstanceOf(EverfiUserSyncLoadException.class)
                .hasMessage("Failed to load Remote Everfi Users.")
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    public void loadRemoteUsersWrapsInvalidRemoteUser() {
        EverfiUserSyncLoader preflight = new EverfiUserSyncLoader(
                new StubEmployeeInfoService(Set.of()),
                new StubEverfiUserClient(List.of(everfiUser("everfi-1", null))),
                new StubEverfiEmployeeMappingDao(List.of())
        );

        assertThatThrownBy(preflight::loadRemoteUsers)
                .isInstanceOf(EverfiUserSyncLoadException.class)
                .hasMessage("Failed to load Remote Everfi Users.")
                .hasCauseInstanceOf(RuntimeException.class);
    }

    @Test
    public void loadRemoteUsersAllowsMissingMapping() {
        EverfiUserSyncLoader preflight = new EverfiUserSyncLoader(
                new StubEmployeeInfoService(Set.of()),
                new StubEverfiUserClient(List.of(everfiUser("everfi-1", "user@example.com"))),
                new StubEverfiEmployeeMappingDao(List.of())
        );

        Set<RemoteUser> remoteUsers = preflight.loadRemoteUsers().remoteUsers();

        RemoteUser remoteUser = assertOne(remoteUsers);
        assertThat(remoteUser.remoteUuid()).isEqualTo("everfi-1");
        assertThat(remoteUser.mapping()).isNull();
    }

    @Test
    public void loadRemoteUsersAssociatesMappingByEverfiUuid() {
        EverfiEmployeeMapping mapping = new EverfiEmployeeMapping(123, "everfi-1");
        EverfiUserSyncLoader preflight = new EverfiUserSyncLoader(
                new StubEmployeeInfoService(Set.of()),
                new StubEverfiUserClient(List.of(everfiUser("everfi-1", "user@example.com"))),
                new StubEverfiEmployeeMappingDao(List.of(mapping))
        );

        Set<RemoteUser> remoteUsers = preflight.loadRemoteUsers().remoteUsers();

        assertThat(assertOne(remoteUsers).mapping()).isEqualTo(mapping);
    }

    @Test
    public void loadRemoteUsers_preservesMissingRemoteEmployeeIdAsNull() {
        EverfiUserSyncLoader preflight = new EverfiUserSyncLoader(
                new StubEmployeeInfoService(Set.of()),
                new StubEverfiUserClient(List.of(everfiUser("everfi-1", "user@example.com", null))),
                new StubEverfiEmployeeMappingDao(List.of())
        );

        Set<RemoteUser> remoteUsers = preflight.loadRemoteUsers().remoteUsers();

        assertThat(assertOne(remoteUsers).remoteEmployeeId()).isNull();
    }

    @Test
    public void loadRemoteUsers_detectsUnmatchedMapping() {
        EverfiEmployeeMapping orphanedMapping = new EverfiEmployeeMapping(123, "everfi-orphan");
        EverfiUserSyncLoader preflight = new EverfiUserSyncLoader(
                new StubEmployeeInfoService(Set.of()),
                new StubEverfiUserClient(List.of()),
                new StubEverfiEmployeeMappingDao(List.of(orphanedMapping))
        );

        EverfiUserSyncLoader.RemoteLoadResult result = preflight.loadRemoteUsers();

        assertThat(result.remoteUsers()).isEmpty();
        assertThat(result.empIdsWithUnmatchedMappings()).containsExactly(123);
    }

    @Test
    public void loadDesiredUsers_populatesCategoryLabels() {
        Employee emp = new Employee();
        emp.setEmployeeId(42);
        emp.setFirstName("Test");
        emp.setLastName("User");
        emp.setEmail("test@nysenate.gov");

        EverfiUserSyncLoader preflight = new EverfiUserSyncLoader(
                new StubEmployeeInfoService(Set.of(emp)),
                new StubEverfiUserClient(List.of()),
                new StubEverfiEmployeeMappingDao(List.of())
        );

        DesiredUser desiredUser = assertOne(preflight.loadDesiredUsers());

        assertThat(desiredUser.employeeId()).isEqualTo(42);
        assertThat(desiredUser.desiredLabels()).containsExactlyInAnyOrder(
                new DesiredLabel(EverfiManagedCategory.ATTENDED_LIVE.categoryName(), "No"),
                new DesiredLabel(EverfiManagedCategory.ROLE.categoryName(), "Employee")
        );
    }

    @Test
    public void loadDesiredUsers_includesDepartmentLabel() {
        Employee emp = employee(42, "NEW_DEPT");
        emp.setFirstName("Test");
        emp.setLastName("User");
        emp.setEmail("test@nysenate.gov");

        EverfiUserSyncLoader preflight = new EverfiUserSyncLoader(
                new StubEmployeeInfoService(Set.of(emp)),
                new StubEverfiUserClient(List.of()),
                new StubEverfiEmployeeMappingDao(List.of())
        );

        DesiredUser desiredUser = assertOne(preflight.loadDesiredUsers());

        assertThat(desiredUser.desiredLabels()).containsExactlyInAnyOrder(
                new DesiredLabel(EverfiManagedCategory.ATTENDED_LIVE.categoryName(), "No"),
                new DesiredLabel(EverfiManagedCategory.DEPARTMENT.categoryName(), "NEW_DEPT"),
                new DesiredLabel(EverfiManagedCategory.ROLE.categoryName(), "Employee")
        );
    }

    @Test
    public void loadDesiredUsers_normalizesDepartmentWhitespace() {
        Employee emp = employee(42, "  NEW_DEPT  ");
        emp.setFirstName("Test");
        emp.setLastName("User");
        emp.setEmail("test@nysenate.gov");

        EverfiUserSyncLoader preflight = new EverfiUserSyncLoader(
                new StubEmployeeInfoService(Set.of(emp)),
                new StubEverfiUserClient(List.of()),
                new StubEverfiEmployeeMappingDao(List.of())
        );

        DesiredUser desiredUser = assertOne(preflight.loadDesiredUsers());

        assertThat(desiredUser.desiredLabels()).contains(
                new DesiredLabel(EverfiManagedCategory.DEPARTMENT.categoryName(), "NEW_DEPT"));
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
            super(null, new EverfiUserPayloadFactory(), new EverfiCategoryService(null));
            this.users = users;
        }

        @Override
        public List<EverfiUser> fetchAll() {
            return users;
        }
    }

    private static class FailingEverfiUserClient extends EverfiUserClient {
        private FailingEverfiUserClient() {
            super(null, new EverfiUserPayloadFactory(), new EverfiCategoryService(null));
        }

        @Override
        public List<EverfiUser> fetchAll() throws IOException {
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

    private static UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException("Not needed for this test.");
    }
}
