package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.pec.everfi.EverfiEmployeeMapping;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategorySnapshot;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(HierarchicalContextRunner.class)
@Category(UnitTest.class)
public class EverfiUserSyncServiceTest {

    @Test
    public void syncUsers_loadsPlansExecutesAndReturnsRun() {
        DesiredUser desiredUser = desiredUser().build();
        RemoteUser remoteUser = remoteUser().build();
        Set<DesiredUser> desiredUsers = Set.of(desiredUser);
        Set<RemoteUser> remoteUsers = Set.of(remoteUser);
        List<PlannedAction> actions = List.of(new PlannedAction(SyncAction.SKIP, desiredUser, remoteUser, List.of()));
        List<SyncResult> results = List.of(SyncResult.skipped(actions.get(0)));

        RecordingPreflight preflight = new RecordingPreflight(desiredUsers, remoteUsers, null);
        RecordingPlanner planner = new RecordingPlanner(actions);
        RecordingExecutor executor = new RecordingExecutor(results);
        EverfiUserSyncService service = new EverfiUserSyncService(preflight, planner, executor);

        SyncRun run = service.syncUsers(true);

        assertThat(preflight.ensureDepartmentLabelsCalls).isEqualTo(1);
        assertThat(preflight.loadDesiredUsersCalls).isEqualTo(1);
        assertThat(preflight.loadRemoteUsersCalls).isEqualTo(1);
        assertThat(preflight.ensureTodaysUploadListLabelCalls).isEqualTo(0);
        assertThat(planner.receivedDesiredUsers).isEqualTo(desiredUsers);
        assertThat(planner.receivedRemoteIndex.getAuthoritativeMatch(1)).contains(remoteUser);
        assertThat(executor.receivedActions).isEqualTo(actions);
        assertThat(executor.receivedUploadListLabel).isNull();
        assertThat(executor.receivedDryRun).isTrue();
        assertThat(run.results()).isEqualTo(results);
        assertThat(run.dryRun()).isTrue();
        assertThat(run.ranAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    public void noCreates_doesNotLoadUploadListLabel() {
        DesiredUser desiredUser = desiredUser().build();
        RemoteUser remoteUser = remoteUser().build();
        List<PlannedAction> actions = List.of(
                new PlannedAction(SyncAction.UPDATE, desiredUser, remoteUser, List.of()));
        List<SyncResult> results = List.of(SyncResult.success(actions.get(0)));

        RecordingPreflight preflight = new RecordingPreflight(Set.of(desiredUser), Set.of(remoteUser), null);
        EverfiUserSyncService service = new EverfiUserSyncService(
                preflight, new RecordingPlanner(actions), new RecordingExecutor(results));

        service.syncUsers(false);

        assertThat(preflight.ensureTodaysUploadListLabelCalls).isEqualTo(0);
    }

    @Test
    public void createsWithDryRun_doesNotLoadUploadListLabel() {
        DesiredUser desiredUser = desiredUser().build();
        List<PlannedAction> actions = List.of(
                new PlannedAction(SyncAction.CREATE, desiredUser, null, List.of()));
        List<SyncResult> results = List.of(SyncResult.success(actions.get(0)));

        RecordingPreflight preflight = new RecordingPreflight(Set.of(desiredUser), Set.of(), null);
        EverfiUserSyncService service = new EverfiUserSyncService(
                preflight, new RecordingPlanner(actions), new RecordingExecutor(results));

        service.syncUsers(true);

        assertThat(preflight.ensureTodaysUploadListLabelCalls).isEqualTo(0);
    }

    @Test
    public void createsWithLiveRun_loadsAndPassesUploadListLabel() {
        EverfiCategoryLabel uploadLabel = new EverfiCategoryLabel(200, "Apr 22 2026");
        DesiredUser desiredUser = desiredUser().build();
        List<PlannedAction> actions = List.of(
                new PlannedAction(SyncAction.CREATE, desiredUser, null, List.of()));
        List<SyncResult> results = List.of(SyncResult.success(actions.get(0)));

        RecordingPreflight preflight = new RecordingPreflight(Set.of(desiredUser), Set.of(), uploadLabel);
        RecordingExecutor executor = new RecordingExecutor(results);
        EverfiUserSyncService service = new EverfiUserSyncService(
                preflight, new RecordingPlanner(actions), executor);

        service.syncUsers(false);

        assertThat(preflight.ensureTodaysUploadListLabelCalls).isEqualTo(1);
        assertThat(executor.receivedUploadListLabel).isSameAs(uploadLabel);
    }

    private DesiredUser.DesiredUserBuilder desiredUser() {
        return DesiredUser.builder()
                .employeeId(1)
                .email("user@nysenate.gov")
                .firstName("Test")
                .lastName("User");
    }

    private RemoteUser.RemoteUserBuilder remoteUser() {
        return RemoteUser.builder()
                .mapping(new EverfiEmployeeMapping(1, "everfi-uuid-1"))
                .remoteUuid("everfi-uuid-1")
                .remoteEmployeeId(1)
                .remoteActive(true)
                .remoteFirstName("Test")
                .remoteLastName("User")
                .remoteEmail("user@nysenate.gov");
    }

    private static class RecordingPreflight extends EverfiUserSyncPreflight {
        private final Set<DesiredUser> desiredUsers;
        private final Set<RemoteUser> remoteUsers;
        private final EverfiCategoryLabel labelToReturn;
        private int ensureDepartmentLabelsCalls;
        private int loadDesiredUsersCalls;
        private int loadRemoteUsersCalls;
        private int ensureTodaysUploadListLabelCalls;

        private RecordingPreflight(Set<DesiredUser> desiredUsers, Set<RemoteUser> remoteUsers,
                                EverfiCategoryLabel labelToReturn) {
            super(null, null, null, null);
            this.desiredUsers = desiredUsers;
            this.remoteUsers = remoteUsers;
            this.labelToReturn = labelToReturn;
        }

        @Override
        EverfiCategorySnapshot loadCategorySnapshot() {
            return new EverfiCategorySnapshot(List.of());
        }

        @Override
        boolean ensureDepartmentLabels(EverfiCategorySnapshot snapshot, boolean dryRun) {
            ensureDepartmentLabelsCalls++;
            return false;
        }

        @Override
        Set<DesiredUser> loadDesiredUsers(EverfiCategorySnapshot snapshot) {
            loadDesiredUsersCalls++;
            return desiredUsers;
        }

        @Override
        EverfiUserSyncPreflight.RemoteLoadResult loadRemoteUsers(EverfiCategorySnapshot snapshot) {
            loadRemoteUsersCalls++;
            return new EverfiUserSyncPreflight.RemoteLoadResult(remoteUsers, Set.of());
        }

        @Override
        EverfiCategoryLabel ensureTodaysUploadListLabel(EverfiCategorySnapshot snapshot) {
            ensureTodaysUploadListLabelCalls++;
            return labelToReturn;
        }
    }

    private static class RecordingPlanner extends EverfiUserSyncPlanner {
        private final List<PlannedAction> actionsToReturn;
        private Set<DesiredUser> receivedDesiredUsers;
        private RemoteUserIndex receivedRemoteIndex;

        private RecordingPlanner(List<PlannedAction> actionsToReturn) {
            this.actionsToReturn = actionsToReturn;
        }

        @Override
        public List<PlannedAction> plan(Set<DesiredUser> desiredUsers, RemoteUserIndex remoteUserIndex) {
            this.receivedDesiredUsers = desiredUsers;
            this.receivedRemoteIndex = remoteUserIndex;
            return actionsToReturn;
        }
    }

    private static class RecordingExecutor extends EverfiUserSyncExecutor {
        private final List<SyncResult> resultsToReturn;
        private List<PlannedAction> receivedActions;
        private EverfiCategoryLabel receivedUploadListLabel;
        private boolean receivedDryRun;

        private RecordingExecutor(List<SyncResult> resultsToReturn) {
            super(null, null);
            this.resultsToReturn = resultsToReturn;
        }

        @Override
        public List<SyncResult> executeAll(List<PlannedAction> actions,
                                           EverfiCategoryLabel uploadListLabel,
                                           boolean dryRun) {
            this.receivedActions = actions;
            this.receivedUploadListLabel = uploadListLabel;
            this.receivedDryRun = dryRun;
            return resultsToReturn;
        }
    }
}
