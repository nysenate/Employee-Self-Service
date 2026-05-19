package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Orchestrates a single end-to-end sync run: preflight → plan → execute.
 * See {@code package-info.java} for the pipeline overview and ubiquitous language.
 *
 * <p>Package-private on purpose: callers should enter through
 * {@link EverfiUserSyncJobService#runUserSync(boolean, boolean)} so scheduling/manual runs share
 * the same synchronized guard and reporting behavior.
 */
@Service
public class EverfiUserSyncService {

    private static final Logger logger = LoggerFactory.getLogger(EverfiUserSyncService.class);

    private final EverfiUserSyncLoader loader;
    private final EverfiUserSyncPlanner planner;
    private final EverfiLabelProvisioner labelProvisioner;
    private final EverfiExecutableActionResolver actionResolver;
    private final EverfiUserSyncExecutor executor;

    public EverfiUserSyncService(
            EverfiUserSyncLoader loader,
            EverfiUserSyncPlanner planner,
            EverfiLabelProvisioner labelProvisioner,
            EverfiExecutableActionResolver actionResolver,
            EverfiUserSyncExecutor executor
    ) {
        this.loader = loader;
        this.planner = planner;
        this.labelProvisioner = labelProvisioner;
        this.actionResolver = actionResolver;
        this.executor = executor;
    }

    /**
     * Runs the pipeline once. When {@code dryRun} is true, no remote or local writes occur, but
     * the returned {@link SyncRun} still reflects what would have happened — including any errors
     * raised during the load and plan stages.
     */
    SyncRun syncUsers(boolean dryRun) {
        loader.initializeCategoryCache();
        var desiredUsers      = loader.loadDesiredUsers();
        var remoteLoadResult  = loader.loadRemoteUsers();
        var actions           = planner.plan(desiredUsers, RemoteUserIndex.from(remoteLoadResult));
        var labelRequirements = LabelRequirements.from(actions);
        var labels            = labelProvisioner.resolve(labelRequirements, dryRun);
        var executableActions = actionResolver.resolve(actions, labels);
        var results           = executor.executeAll(executableActions, dryRun);
        return SyncRun.of(results, dryRun);
    }
}
