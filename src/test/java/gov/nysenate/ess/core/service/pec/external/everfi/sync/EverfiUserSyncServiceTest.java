package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.pec.everfi.EverfiEmployeeMapping;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        List<ExecutableAction> executableActions = List.of(new ExecutableAction(actions.get(0), List.of(), null));
        List<SyncResult> results = List.of(SyncResult.skipped(actions.get(0)));
        ResolvedLabels labels = ResolvedLabels.empty();

        RecordingCategoryService categoryService = new RecordingCategoryService();
        RecordingLoader preflight = new RecordingLoader(desiredUsers, remoteUsers);
        RecordingPlanner planner = new RecordingPlanner(actions);
        RecordingLabelProvisioner provisioner = new RecordingLabelProvisioner(labels);
        RecordingActionResolver actionResolver = new RecordingActionResolver(executableActions);
        RecordingExecutor executor = new RecordingExecutor(results);
        EverfiUserSyncService service = new EverfiUserSyncService(
                categoryService, preflight, planner, provisioner, actionResolver, executor);

        SyncRun run = service.syncUsers(true);

        assertThat(categoryService.initializeCalls).isEqualTo(1);
        assertThat(preflight.loadDesiredUsersCalls).isEqualTo(1);
        assertThat(preflight.loadRemoteUsersCalls).isEqualTo(1);
        assertThat(planner.receivedDesiredUsers).isEqualTo(desiredUsers);
        assertThat(planner.receivedRemoteIndex.getAuthoritativeMatch(1)).contains(remoteUser);
        assertThat(provisioner.receivedRequirements).isEqualTo(new LabelRequirements(Set.of(), false));
        assertThat(provisioner.receivedDryRun).isTrue();
        assertThat(actionResolver.receivedActions).isEqualTo(actions);
        assertThat(actionResolver.receivedLabels).isSameAs(labels);
        assertThat(executor.receivedActions).isEqualTo(executableActions);
        assertThat(executor.receivedDryRun).isTrue();
        assertThat(run.results()).isEqualTo(results);
        assertThat(run.dryRun()).isTrue();
        assertThat(run.ranAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    public void syncUsers_provisionerReceivesActionsAndDryRun() {
        DesiredUser desiredUser = desiredUser().build();
        List<PlannedAction> actions = List.of(
                new PlannedAction(SyncAction.CREATE, desiredUser, null, List.of()));
        List<ExecutableAction> executableActions = List.of(new ExecutableAction(actions.get(0), List.of(), null));
        List<SyncResult> results = List.of(SyncResult.success(actions.get(0)));
        ResolvedLabels labels = ResolvedLabels.empty();

        RecordingLoader preflight = new RecordingLoader(Set.of(desiredUser), Set.of());
        RecordingLabelProvisioner provisioner = new RecordingLabelProvisioner(labels);
        RecordingActionResolver actionResolver = new RecordingActionResolver(executableActions);
        RecordingExecutor executor = new RecordingExecutor(results);
        EverfiUserSyncService service = new EverfiUserSyncService(
                new RecordingCategoryService(), preflight, new RecordingPlanner(actions), provisioner,
                actionResolver, executor);

        service.syncUsers(false);

        assertThat(provisioner.receivedRequirements.uploadListRequired()).isTrue();
        assertThat(provisioner.receivedDryRun).isFalse();
        assertThat(actionResolver.receivedLabels).isSameAs(labels);
        assertThat(executor.receivedActions).isSameAs(executableActions);
    }

    @Test
    public void initializeCategoryCacheWrapsFailure() {
        EverfiUserSyncService service = new EverfiUserSyncService(
                new FailingCategoryService(),
                new RecordingLoader(Set.of(), Set.of()),
                new RecordingPlanner(List.of()),
                new RecordingLabelProvisioner(ResolvedLabels.empty()),
                new RecordingActionResolver(List.of()),
                new RecordingExecutor(List.of())
        );

        assertThatThrownBy(service::initializeCategoryCache)
                .isInstanceOf(EverfiUserSyncLoadException.class)
                .hasMessage("Failed to initialize Everfi category cache.")
                .hasCauseInstanceOf(IOException.class);
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

    private static class RecordingLoader extends EverfiUserSyncLoader {
        private final Set<DesiredUser> desiredUsers;
        private final Set<RemoteUser> remoteUsers;
        private int loadDesiredUsersCalls;
        private int loadRemoteUsersCalls;

        private RecordingLoader(Set<DesiredUser> desiredUsers, Set<RemoteUser> remoteUsers) {
            super(null, null, null);
            this.desiredUsers = desiredUsers;
            this.remoteUsers = remoteUsers;
        }

        @Override
        Set<DesiredUser> loadDesiredUsers() {
            loadDesiredUsersCalls++;
            return desiredUsers;
        }

        @Override
        EverfiUserSyncLoader.RemoteLoadResult loadRemoteUsers() {
            loadRemoteUsersCalls++;
            return new EverfiUserSyncLoader.RemoteLoadResult(remoteUsers, Set.of());
        }
    }

    private static class RecordingCategoryService extends EverfiCategoryService {
        private int initializeCalls;

        private RecordingCategoryService() {
            super(null);
        }

        @Override
        public void initialize() {
            initializeCalls++;
        }
    }

    private static class FailingCategoryService extends EverfiCategoryService {
        private FailingCategoryService() {
            super(null);
        }

        @Override
        public void initialize() throws IOException {
            throw new IOException("Failed to fetch categories.");
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

    private static class RecordingLabelProvisioner extends EverfiLabelProvisioner {
        private final ResolvedLabels resultToReturn;
        private LabelRequirements receivedRequirements;
        private boolean receivedDryRun;

        private RecordingLabelProvisioner(ResolvedLabels resultToReturn) {
            super(null);
            this.resultToReturn = resultToReturn;
        }

        @Override
        ResolvedLabels resolve(LabelRequirements requirements, boolean dryRun) {
            this.receivedRequirements = requirements;
            this.receivedDryRun = dryRun;
            return resultToReturn;
        }
    }

    private static class RecordingActionResolver extends EverfiExecutableActionResolver {
        private final List<ExecutableAction> actionsToReturn;
        private List<PlannedAction> receivedActions;
        private ResolvedLabels receivedLabels;

        private RecordingActionResolver(List<ExecutableAction> actionsToReturn) {
            this.actionsToReturn = actionsToReturn;
        }

        @Override
        List<ExecutableAction> resolve(List<PlannedAction> actions, ResolvedLabels labels) {
            this.receivedActions = actions;
            this.receivedLabels = labels;
            return actionsToReturn;
        }
    }

    private static class RecordingExecutor extends EverfiUserSyncExecutor {
        private final List<SyncResult> resultsToReturn;
        private List<ExecutableAction> receivedActions;
        private boolean receivedDryRun;

        private RecordingExecutor(List<SyncResult> resultsToReturn) {
            super(null, null);
            this.resultsToReturn = resultsToReturn;
        }

        @Override
        List<SyncResult> executeAll(List<ExecutableAction> actions, boolean dryRun) {
            this.receivedActions = actions;
            this.receivedDryRun = dryRun;
            return resultsToReturn;
        }
    }
}
