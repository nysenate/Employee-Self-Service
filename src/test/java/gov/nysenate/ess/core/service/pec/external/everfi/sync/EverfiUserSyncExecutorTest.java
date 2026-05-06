package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.pec.everfi.EverfiEmployeeMapping;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;
import gov.nysenate.ess.core.service.pec.external.everfi.user.EverfiAddUserCommand;
import gov.nysenate.ess.core.service.pec.external.everfi.user.EverfiUpdateUserCommand;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.dao.DataAccessResourceFailureException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static gov.nysenate.ess.core.service.pec.external.everfi.sync.EverfiUserSyncExecutorTestSupport.createAction;
import static gov.nysenate.ess.core.service.pec.external.everfi.sync.EverfiUserSyncExecutorTestSupport.desiredUser;
import static gov.nysenate.ess.core.service.pec.external.everfi.sync.EverfiUserSyncExecutorTestSupport.label;
import static gov.nysenate.ess.core.service.pec.external.everfi.sync.EverfiUserSyncExecutorTestSupport.remoteUser;

@RunWith(HierarchicalContextRunner.class)
@Category(UnitTest.class)
public class EverfiUserSyncExecutorTest {

    private EverfiUserSyncExecutorTestSupport.RecordingEverfiUserClient userClient;
    private EverfiUserSyncExecutorTestSupport.RecordingEverfiEmployeeMappingDao mappingDao;
    private EverfiUserSyncExecutor executor;

    @Before
    public void setup() {
        userClient = EverfiUserSyncExecutorTestSupport.recordingUserClient();
        mappingDao = EverfiUserSyncExecutorTestSupport.recordingMappingDao();
        executor = new EverfiUserSyncExecutor(userClient, mappingDao);
    }

    @Test
    public void emptyActions_returnsNoResults() {
        assertThat(executor.executeAll(List.of(), null, false)).isEmpty();
    }

    public class CreateAction {

        @Test
        public void createsEverfiUserAndMapping() {
            PlannedAction action = createAction();

            List<SyncResult> results = executor.executeAll(List.of(action), null, false);

            assertThat(userClient.addedUserCommand).isEqualTo(new EverfiAddUserCommand(
                    1, "Test", "User", "user@nysenate.gov", List.of()));
            assertThat(mappingDao.insertedMappings)
                    .containsExactly(new EverfiEmployeeMapping(1, "everfi-uuid-1"));
            assertNoUpdatedUserWrite();
            assertResults(results, SyncResult.success(action));
        }

        @Test
        public void withUploadListLabel_appendsItToDesiredLabels() {
            EverfiCategoryLabel roleLabel = label(100, "Role", "Employee");
            EverfiCategoryLabel uploadLabel = label(200, "Upload List", "Apr 22 2026");
            PlannedAction action = new PlannedAction(SyncAction.CREATE,
                    desiredUser().categoryLabels(List.of(roleLabel)).build(), null, List.of());

            executor.executeAll(List.of(action), uploadLabel, false);

            assertThat(userClient.addedUserCommand.categoryLabels())
                    .containsExactly(roleLabel, uploadLabel);
        }

        @Test
        public void respectsDryRun() {
            PlannedAction action = createAction();

            List<SyncResult> results = executor.executeAll(List.of(action), null, true);

            assertNoWrites();
            assertResults(results, SyncResult.success(action));
        }

        @Test
        public void onError_returnsError() {
            executor = new EverfiUserSyncExecutor(
                    EverfiUserSyncExecutorTestSupport.FailingEverfiUserClient.failingOnCreate(),
                    mappingDao
            );
            PlannedAction action = createAction();

            List<SyncResult> results = executor.executeAll(List.of(action), null, false);

            assertNoWrites();
            assertResults(results, SyncResult.error(action, "Error making Everfi request"));
        }

        @Test
        public void missingFirstName_returnsError() {
            PlannedAction action = new PlannedAction(
                    SyncAction.CREATE,
                    desiredUser().firstName(" ").build(),
                    null,
                    List.of()
            );

            List<SyncResult> results = executor.executeAll(List.of(action), null, false);

            assertNoWrites();
            assertResults(results, SyncResult.error(action, "firstName must not be empty"));
        }

        @Test
        public void nullEverfiUserResponse_returnsError() {
            executor = new EverfiUserSyncExecutor(
                    new EverfiUserSyncExecutorTestSupport.RecordingEverfiUserClient(null),
                    mappingDao
            );
            PlannedAction action = createAction();

            List<SyncResult> results = executor.executeAll(List.of(action), null, false);

            assertNoUpdatedUserWrite();
            assertThat(mappingDao.insertedMappings).isEmpty();
            assertResults(results, SyncResult.error(action, "Everfi API returned null user"));
        }

        @Test
        public void nullEverfiUuid_returnsError() {
            executor = new EverfiUserSyncExecutor(
                    new EverfiUserSyncExecutorTestSupport.RecordingEverfiUserClient(
                            new EverfiUserSyncExecutorTestSupport.TestEverfiUser(null, "user@nysenate.gov")
                    ),
                    mappingDao
            );
            PlannedAction action = createAction();

            List<SyncResult> results = executor.executeAll(List.of(action), null, false);

            assertNoUpdatedUserWrite();
            assertThat(mappingDao.insertedMappings).isEmpty();
            assertResults(results, SyncResult.error(action, "Everfi API returned user without uuid"));
        }

        @Test
        public void mappingInsertFailure_returnsError() {
            mappingDao.failInsertWith(new DataAccessResourceFailureException("Failed to insert mapping"));
            PlannedAction action = createAction();

            List<SyncResult> results = executor.executeAll(List.of(action), null, false);

            assertNoUpdatedUserWrite();
            assertThat(mappingDao.insertedMappings).isEmpty();
            assertResults(results, SyncResult.error(action, "Failed to insert mapping"));
        }
    }

    public class SkipAction {

        @Test
        public void doesNotWriteAndReturnsSkipped() {
            PlannedAction action = new PlannedAction(SyncAction.SKIP, desiredUser().build(), remoteUser().build(), List.of());

            List<SyncResult> results = executor.executeAll(List.of(action), null, false);

            assertNoWrites();
            assertResults(results, SyncResult.skipped(action));
        }

        @Test
        public void withoutDesiredUser_doesNotWriteAndReturnsSkipped() {
            PlannedAction action = new PlannedAction(SyncAction.SKIP, null, remoteUser().remoteActive(false).build(), List.of());

            List<SyncResult> results = executor.executeAll(List.of(action), null, false);

            assertNoWrites();
            assertResults(results, SyncResult.skipped(action));
        }
    }

    public class FlagAction {

        @Test
        public void doesNotWriteAndReturnsFlagged() {
            PlannedAction action = new PlannedAction(
                    SyncAction.FLAG,
                    desiredUser().build(),
                    remoteUser().build(),
                    List.of(SyncIssue.UNMAPPED_REMOTE_USER)
            );

            List<SyncResult> results = executor.executeAll(List.of(action), null, false);

            assertNoWrites();
            assertResults(results, SyncResult.flagged(action));
        }
    }

    public class UpdateAction {

        @Test
        public void usesDesiredFieldsAndMappingUuid() {
            PlannedAction action = new PlannedAction(
                    SyncAction.UPDATE,
                    desiredUser().firstName("Updated").email("updated@nysenate.gov").build(),
                    remoteUser().remoteFirstName("Stale").remoteEmail("stale@nysenate.gov").build(),
                    List.of()
            );

            List<SyncResult> results = executor.executeAll(List.of(action), null, false);

            assertNoAddUserWrite();
            assertThat(mappingDao.insertedMappings).isEmpty();
            assertThat(userClient.updatedUserCommand).isEqualTo(EverfiUpdateUserCommand.builder()
                    .uuid("everfi-uuid-1")
                    .employeeId(1)
                    .firstName("Updated")
                    .lastName("User")
                    .email("updated@nysenate.gov")
                    .active(true)
                    .categoryLabels(List.of())
                    .build());
            assertResults(results, SyncResult.success(action));
        }

        @Test
        public void withPersonalEmailOverride_preservesRemoteEmail() {
            PlannedAction action = new PlannedAction(
                    SyncAction.UPDATE,
                    desiredUser()
                            .firstName("Updated")
                            .lastName("Person")
                            .email("updated@nysenate.gov")
                            .build(),
                    remoteUser()
                            .remoteFirstName("Stale")
                            .remoteLastName("User")
                            .remoteEmail("person@example.com")
                            .build(),
                    List.of()
            );

            List<SyncResult> results = executor.executeAll(List.of(action), null, false);

            assertThat(userClient.updatedUserCommand).isEqualTo(EverfiUpdateUserCommand.builder()
                    .uuid("everfi-uuid-1")
                    .employeeId(1)
                    .firstName("Updated")
                    .lastName("Person")
                    .email("person@example.com")
                    .active(true)
                    .categoryLabels(List.of())
                    .build());
            assertResults(results, SyncResult.success(action));
        }

        @Test
        public void preservesAllNonManagedRemoteLabels() {
            EverfiCategoryLabel desiredRoleLabel = label(100, "Role", "Employee");
            EverfiCategoryLabel remoteUploadLabel = label(200, "Upload List", "Apr 1 2026");
            EverfiCategoryLabel remoteAdminLabel  = label(300, "Custom Category", "Admin Added");
            PlannedAction action = new PlannedAction(
                    SyncAction.UPDATE,
                    desiredUser().categoryLabels(List.of(desiredRoleLabel)).build(),
                    remoteUser().categoryLabels(List.of(remoteUploadLabel, remoteAdminLabel)).build(),
                    List.of()
            );

            executor.executeAll(List.of(action), null, false);

            assertThat(userClient.updatedUserCommand.categoryLabels())
                    .containsExactlyInAnyOrder(desiredRoleLabel, remoteUploadLabel, remoteAdminLabel);
        }

        @Test
        public void replacesStaleRemoteLabelForManagedCategory() {
            EverfiCategoryLabel desiredRoleEmployee = label(100, "Role", "Employee");
            EverfiCategoryLabel remoteStaleSenator  = label(101, "Role", "Senator");
            EverfiCategoryLabel remoteUploadLabel   = label(200, "Upload List", "Apr 1 2026");
            PlannedAction action = new PlannedAction(
                    SyncAction.UPDATE,
                    desiredUser().categoryLabels(List.of(desiredRoleEmployee)).build(),
                    remoteUser().categoryLabels(List.of(remoteStaleSenator, remoteUploadLabel)).build(),
                    List.of()
            );

            executor.executeAll(List.of(action), null, false);

            assertThat(userClient.updatedUserCommand.categoryLabels())
                    .containsExactlyInAnyOrder(desiredRoleEmployee, remoteUploadLabel);
        }

        @Test
        public void respectsDryRun() {
            PlannedAction action = new PlannedAction(
                    SyncAction.UPDATE,
                    desiredUser().firstName("Updated").email("updated@nysenate.gov").build(),
                    remoteUser().build(),
                    List.of()
            );

            List<SyncResult> results = executor.executeAll(List.of(action), null, true);

            assertNoWrites();
            assertResults(results, SyncResult.success(action));
        }

        @Test
        public void onError_returnsError() {
            executor = new EverfiUserSyncExecutor(
                    EverfiUserSyncExecutorTestSupport.FailingEverfiUserClient.failingOnUpdate(),
                    mappingDao
            );
            PlannedAction action = new PlannedAction(
                    SyncAction.UPDATE,
                    desiredUser().build(),
                    remoteUser().build(),
                    List.of()
            );

            List<SyncResult> results = executor.executeAll(List.of(action), null, false);

            assertNoWrites();
            assertResults(results, SyncResult.error(action, "Error making Everfi request"));
        }

        @Test
        public void missingLastName_returnsError() {
            PlannedAction action = new PlannedAction(
                    SyncAction.UPDATE,
                    desiredUser().lastName(" ").build(),
                    remoteUser().build(),
                    List.of()
            );

            List<SyncResult> results = executor.executeAll(List.of(action), null, false);

            assertNoWrites();
            assertResults(results, SyncResult.error(action, "lastName must not be empty"));
        }

        @Test
        public void nullEverfiResponse_returnsError() {
            executor = new EverfiUserSyncExecutor(
                    new EverfiUserSyncExecutorTestSupport.FailingEverfiUserClient(false, false),
                    mappingDao
            );
            PlannedAction action = new PlannedAction(
                    SyncAction.UPDATE,
                    desiredUser().build(),
                    remoteUser().build(),
                    List.of()
            );

            List<SyncResult> results = executor.executeAll(List.of(action), null, false);

            assertNoWrites();
            assertResults(results, SyncResult.error(action, "Everfi API returned null user"));
        }
    }

    public class ReactivateAction {

        @Test
        public void usesDesiredFieldsAndMappingUuidAndActivatesUser() {
            PlannedAction action = new PlannedAction(
                    SyncAction.REACTIVATE,
                    desiredUser().firstName("Updated").email("updated@nysenate.gov").build(),
                    remoteUser().remoteActive(false).remoteFirstName("Stale").remoteEmail("stale@nysenate.gov").build(),
                    List.of()
            );

            List<SyncResult> results = executor.executeAll(List.of(action), null, false);

            assertNoAddUserWrite();
            assertThat(mappingDao.insertedMappings).isEmpty();
            assertThat(userClient.updatedUserCommand).isEqualTo(EverfiUpdateUserCommand.builder()
                    .uuid("everfi-uuid-1")
                    .employeeId(1)
                    .firstName("Updated")
                    .lastName("User")
                    .email("updated@nysenate.gov")
                    .active(true)
                    .categoryLabels(List.of())
                    .build());
            assertResults(results, SyncResult.success(action));
        }

        @Test
        public void withDeactivatedEmail_restoresDesiredEmailAndActivatesUser() {
            PlannedAction action = new PlannedAction(
                    SyncAction.REACTIVATE,
                    desiredUser()
                            .firstName("Reactivated")
                            .lastName("Person")
                            .email("reactivated@nysenate.gov")
                            .build(),
                    remoteUser()
                            .remoteActive(false)
                            .remoteFirstName("Old")
                            .remoteLastName("Name")
                            .remoteEmail("deactivated-1@nysenate.invalid")
                            .build(),
                    List.of()
            );

            executor.executeAll(List.of(action), null, false);

            assertThat(userClient.updatedUserCommand).isEqualTo(EverfiUpdateUserCommand.builder()
                    .uuid("everfi-uuid-1")
                    .employeeId(1)
                    .firstName("Reactivated")
                    .lastName("Person")
                    .email("reactivated@nysenate.gov")
                    .active(true)
                    .categoryLabels(List.of())
                    .build());
        }

        @Test
        public void preservesAllNonManagedRemoteLabels() {
            EverfiCategoryLabel desiredRoleLabel = label(100, "Role", "Employee");
            EverfiCategoryLabel remoteUploadLabel = label(200, "Upload List", "Apr 1 2026");
            EverfiCategoryLabel remoteAdminLabel  = label(300, "Custom Category", "Admin Added");
            PlannedAction action = new PlannedAction(
                    SyncAction.REACTIVATE,
                    desiredUser().categoryLabels(List.of(desiredRoleLabel)).build(),
                    remoteUser().remoteActive(false).categoryLabels(List.of(remoteUploadLabel, remoteAdminLabel)).build(),
                    List.of()
            );

            executor.executeAll(List.of(action), null, false);

            assertThat(userClient.updatedUserCommand.categoryLabels())
                    .containsExactlyInAnyOrder(desiredRoleLabel, remoteUploadLabel, remoteAdminLabel);
        }

        @Test
        public void replacesStaleRemoteLabelForManagedCategory() {
            EverfiCategoryLabel desiredRoleEmployee = label(100, "Role", "Employee");
            EverfiCategoryLabel remoteStaleSenator  = label(101, "Role", "Senator");
            EverfiCategoryLabel remoteUploadLabel   = label(200, "Upload List", "Apr 1 2026");
            EverfiCategoryLabel remoteAdminLabel    = label(300, "Custom Category", "Admin Added");
            PlannedAction action = new PlannedAction(
                    SyncAction.REACTIVATE,
                    desiredUser().categoryLabels(List.of(desiredRoleEmployee)).build(),
                    remoteUser().remoteActive(false)
                            .categoryLabels(List.of(remoteStaleSenator, remoteUploadLabel, remoteAdminLabel))
                            .build(),
                    List.of()
            );

            executor.executeAll(List.of(action), null, false);

            assertThat(userClient.updatedUserCommand.categoryLabels())
                    .containsExactlyInAnyOrder(desiredRoleEmployee, remoteUploadLabel, remoteAdminLabel);
        }

        @Test
        public void respectsDryRun() {
            PlannedAction action = new PlannedAction(
                    SyncAction.REACTIVATE,
                    desiredUser().build(),
                    remoteUser().remoteActive(false).build(),
                    List.of()
            );

            List<SyncResult> results = executor.executeAll(List.of(action), null, true);

            assertNoWrites();
            assertResults(results, SyncResult.success(action));
        }

        @Test
        public void withoutEmail_returnsErrorAndDoesNotWrite() {
            PlannedAction action = new PlannedAction(
                    SyncAction.REACTIVATE,
                    desiredUser().email(null).build(),
                    remoteUser().remoteActive(false).build(),
                    List.of(SyncIssue.MISSING_EMAIL)
            );

            List<SyncResult> results = executor.executeAll(List.of(action), null, false);

            assertNoWrites();
            assertResults(results, SyncResult.error(action, "Cannot reactivate user without email"));
        }

        @Test
        public void onError_returnsError() {
            executor = new EverfiUserSyncExecutor(
                    EverfiUserSyncExecutorTestSupport.FailingEverfiUserClient.failingOnUpdate(),
                    mappingDao
            );
            PlannedAction action = new PlannedAction(
                    SyncAction.REACTIVATE,
                    desiredUser().build(),
                    remoteUser().remoteActive(false).build(),
                    List.of()
            );

            List<SyncResult> results = executor.executeAll(List.of(action), null, false);

            assertNoWrites();
            assertResults(results, SyncResult.error(action, "Error making Everfi request"));
        }
    }

    public class DeactivateAction {

        @Test
        public void usesRemoteFieldsAndMappingUuidAndDeactivatesUser() {
            PlannedAction action = new PlannedAction(
                    SyncAction.DEACTIVATE,
                    null,
                    remoteUser().remoteFirstName("Test").remoteLastName("User").remoteEmail("user@nysenate.gov").build(),
                    List.of()
            );

            List<SyncResult> results = executor.executeAll(List.of(action), null, false);

            assertNoAddUserWrite();
            assertThat(mappingDao.insertedMappings).isEmpty();
            assertThat(userClient.updatedUserCommand).isEqualTo(EverfiUpdateUserCommand.builder()
                    .uuid("everfi-uuid-1")
                    .employeeId(1)
                    .firstName("Test")
                    .lastName("User")
                    .email("deactivated-1@nysenate.invalid")
                    .active(false)
                    .categoryLabels(List.of())
                    .build());
            assertResults(results, SyncResult.success(action));
        }

        @Test
        public void deactivatingRemoteUser_setsInactivePlaceholderEmail() {
            PlannedAction action = new PlannedAction(
                    SyncAction.DEACTIVATE,
                    null,
                    remoteUser().remoteEmail("already-stale@nysenate.govx").build(),
                    List.of()
            );

            executor.executeAll(List.of(action), null, false);

            assertThat(userClient.updatedUserCommand.email()).isEqualTo("deactivated-1@nysenate.invalid");
            assertThat(userClient.updatedUserCommand.active()).isFalse();
        }

        @Test
        public void passesRemoteLabelsUnchanged() {
            EverfiCategoryLabel remoteLabel = label(100, "Role", "Employee");
            PlannedAction action = new PlannedAction(
                    SyncAction.DEACTIVATE,
                    null,
                    remoteUser().categoryLabels(List.of(remoteLabel)).build(),
                    List.of()
            );

            executor.executeAll(List.of(action), null, false);

            assertThat(userClient.updatedUserCommand.categoryLabels()).containsExactly(remoteLabel);
        }

        @Test
        public void respectsDryRun() {
            PlannedAction action = new PlannedAction(
                    SyncAction.DEACTIVATE, null, remoteUser().build(), List.of());

            List<SyncResult> results = executor.executeAll(List.of(action), null, true);

            assertNoWrites();
            assertResults(results, SyncResult.success(action));
        }

        @Test
        public void onError_returnsError() {
            executor = new EverfiUserSyncExecutor(
                    EverfiUserSyncExecutorTestSupport.FailingEverfiUserClient.failingOnUpdate(),
                    mappingDao
            );
            PlannedAction action = new PlannedAction(
                    SyncAction.DEACTIVATE, null, remoteUser().build(), List.of());

            List<SyncResult> results = executor.executeAll(List.of(action), null, false);

            assertNoWrites();
            assertResults(results, SyncResult.error(action, "Error making Everfi request"));
        }

        @Test
        public void missingRemoteFirstName_returnsError() {
            PlannedAction action = new PlannedAction(
                    SyncAction.DEACTIVATE,
                    null,
                    remoteUser().remoteFirstName(" ").build(),
                    List.of()
            );

            List<SyncResult> results = executor.executeAll(List.of(action), null, false);

            assertNoWrites();
            assertResults(results, SyncResult.error(action, "firstName must not be empty"));
        }

        @Test
        public void missingRemoteLastName_returnsError() {
            PlannedAction action = new PlannedAction(
                    SyncAction.DEACTIVATE,
                    null,
                    remoteUser().remoteLastName(" ").build(),
                    List.of()
            );

            List<SyncResult> results = executor.executeAll(List.of(action), null, false);

            assertNoWrites();
            assertResults(results, SyncResult.error(action, "lastName must not be empty"));
        }
    }

    @Test
    public void mixedActions_preserveResultOrder() {
        PlannedAction create = createAction();
        PlannedAction skip   = new PlannedAction(SyncAction.SKIP, desiredUser().build(), null, List.of());
        PlannedAction flag   = new PlannedAction(SyncAction.FLAG, null, remoteUser().build(), List.of(SyncIssue.UNRECOGNIZED_ACTIVE_REMOTE));
        PlannedAction update = new PlannedAction(SyncAction.UPDATE, desiredUser().build(), remoteUser().build(), List.of());

        List<SyncResult> results = executor.executeAll(List.of(create, skip, flag, update), null, false);

        assertThat(results).containsExactly(
                SyncResult.success(create),
                SyncResult.skipped(skip),
                SyncResult.flagged(flag),
                SyncResult.success(update)
        );
    }

    @Test
    public void errorOnOneAction_doesNotAbortLaterActions() {
        PlannedAction create = new PlannedAction(
                SyncAction.CREATE,
                desiredUser().firstName(null).build(),
                null,
                List.of()
        );
        PlannedAction update = new PlannedAction(
                SyncAction.UPDATE,
                desiredUser().firstName("Updated").build(),
                remoteUser().build(),
                List.of()
        );

        List<SyncResult> results = executor.executeAll(List.of(create, update), null, false);

        assertThat(userClient.updatedUserCommand).isEqualTo(EverfiUpdateUserCommand.builder()
                .uuid("everfi-uuid-1")
                .employeeId(1)
                .firstName("Updated")
                .lastName("User")
                .email("user@nysenate.gov")
                .active(true)
                .categoryLabels(List.of())
                .build());
        assertThat(mappingDao.insertedMappings).isEmpty();
        assertThat(results).containsExactly(
                SyncResult.error(create, "firstName must not be empty"),
                SyncResult.success(update)
        );
    }

    private void assertNoWrites() {
        assertNoAddUserWrite();
        assertNoUpdatedUserWrite();
        assertThat(mappingDao.insertedMappings).isEmpty();
    }

    private void assertNoAddUserWrite() {
        assertThat(userClient.addedUserCommand).isNull();
    }

    private void assertNoUpdatedUserWrite() {
        assertThat(userClient.updatedUserCommand).isNull();
    }

    private void assertResults(List<SyncResult> actualResults, SyncResult... expectedResults) {
        assertThat(actualResults).containsExactly(expectedResults);
    }
}
