package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import gov.nysenate.ess.core.dao.pec.everfi.EverfiEmployeeMappingDao;
import gov.nysenate.ess.core.model.pec.everfi.EverfiEmployeeMapping;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;
import gov.nysenate.ess.core.service.pec.external.everfi.user.EverfiAddUserCommand;
import gov.nysenate.ess.core.service.pec.external.everfi.user.EverfiUpdateUserCommand;
import gov.nysenate.ess.core.service.pec.external.everfi.user.EverfiUser;
import gov.nysenate.ess.core.service.pec.external.everfi.user.EverfiUserClient;
import gov.nysenate.ess.core.service.pec.external.everfi.user.EverfiUserPayloadFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

final class EverfiUserSyncExecutorTestSupport {

    private EverfiUserSyncExecutorTestSupport() {}

    static RecordingEverfiUserClient recordingUserClient() {
        return new RecordingEverfiUserClient(new TestEverfiUser("everfi-uuid-1", "user@nysenate.gov"));
    }

    static RecordingEverfiEmployeeMappingDao recordingMappingDao() {
        return new RecordingEverfiEmployeeMappingDao();
    }

    static DesiredUser.DesiredUserBuilder desiredUser() {
        return DesiredUser.builder()
                .employeeId(1)
                .email("user@nysenate.gov")
                .firstName("Test")
                .lastName("User");
    }

    static RemoteUser.RemoteUserBuilder remoteUser() {
        return RemoteUser.builder()
                .mapping(new EverfiEmployeeMapping(1, "everfi-uuid-1"))
                .remoteUuid("everfi-uuid-1")
                .remoteEmployeeId(1)
                .remoteActive(true)
                .remoteFirstName("Test")
                .remoteLastName("User")
                .remoteEmail("user@nysenate.gov");
    }

    static PlannedAction createAction() {
        return new PlannedAction(SyncAction.CREATE, desiredUser().build(), null, List.of());
    }

    static EverfiCategoryLabel label(int labelId, String categoryName, String labelName) {
        EverfiCategoryLabel label = new EverfiCategoryLabel(labelId, labelName);
        label.setCategoryName(categoryName);
        return label;
    }

    static final class RecordingEverfiUserClient extends EverfiUserClient {
        EverfiAddUserCommand addedUserCommand;
        EverfiUpdateUserCommand updatedUserCommand;
        private final EverfiUser userToReturn;

        RecordingEverfiUserClient(EverfiUser userToReturn) {
            super(null, new EverfiUserPayloadFactory());
            this.userToReturn = userToReturn;
        }

        @Override
        public EverfiUser addUser(EverfiAddUserCommand command) {
            this.addedUserCommand = command;
            return userToReturn;
        }

        @Override
        public EverfiUser updateUser(EverfiUpdateUserCommand command) {
            this.updatedUserCommand = command;
            return userToReturn;
        }
    }

    static final class FailingEverfiUserClient extends EverfiUserClient {
        private final boolean failAddUser;
        private final boolean failUpdateUser;

        FailingEverfiUserClient(boolean failAddUser, boolean failUpdateUser) {
            super(null, new EverfiUserPayloadFactory());
            this.failAddUser = failAddUser;
            this.failUpdateUser = failUpdateUser;
        }

        static FailingEverfiUserClient failingOnCreate() {
            return new FailingEverfiUserClient(true, false);
        }

        static FailingEverfiUserClient failingOnUpdate() {
            return new FailingEverfiUserClient(false, true);
        }

        @Override
        public EverfiUser addUser(EverfiAddUserCommand command) throws IOException {
            if (failAddUser) {
                throw new IOException("Error making Everfi request");
            }
            return null;
        }

        @Override
        public EverfiUser updateUser(EverfiUpdateUserCommand command) throws IOException {
            if (failUpdateUser) {
                throw new IOException("Error making Everfi request");
            }
            return null;
        }
    }

    static final class RecordingEverfiEmployeeMappingDao implements EverfiEmployeeMappingDao {
        final List<EverfiEmployeeMapping> insertedMappings = new ArrayList<>();
        private RuntimeException insertFailure;

        @Override
        public List<EverfiEmployeeMapping> findAll() {
            throw unsupported();
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
            if (insertFailure != null) {
                throw insertFailure;
            }
            insertedMappings.add(mapping);
            return 1;
        }

        void failInsertWith(RuntimeException insertFailure) {
            this.insertFailure = insertFailure;
        }
    }

    static final class TestEverfiUser extends EverfiUser {
        private final String uuid;
        private final String email;

        TestEverfiUser(String uuid, String email) {
            this.uuid = uuid;
            this.email = email;
        }

        @Override public String getUuid() { return uuid; }
        @Override public Integer getEmployeeId() { return 1; }
        @Override public boolean isActive() { return true; }
        @Override public String getEmail() { return email; }
        @Override public String getFirstName() { return "Test"; }
        @Override public String getLastName() { return "User"; }
    }

    private static UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException("Not needed for this test.");
    }
}
