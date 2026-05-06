package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Orchestrates a single end-to-end sync run: load → plan → execute.
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
    private final EverfiUserSyncExecutor executor;

    public EverfiUserSyncService(
            EverfiUserSyncLoader loader,
            EverfiUserSyncPlanner planner,
            EverfiUserSyncExecutor executor
    ) {
        this.loader = loader;
        this.planner = planner;
        this.executor = executor;
    }

    /**
     * Runs the pipeline once. When {@code dryRun} is true, no remote or local writes occur, but
     * the returned {@link SyncRun} still reflects what would have happened — including any errors
     * raised during the load and plan stages.
     */
    SyncRun syncUsers(boolean dryRun) {
        loader.bootstrapDepartmentLabels(dryRun);
        var desiredUsers = loader.loadDesiredUsers();
        var remoteLoadResult = loader.loadRemoteUsers();

        var actions = planner.plan(desiredUsers, RemoteUserIndex.from(
                remoteLoadResult.remoteUsers(),
                remoteLoadResult.empIdsWithUnmatchedMappings()
        ));

        // Only find/create the Upload List label if there are actually users to create,
        // and only on a live run — dry runs must not create labels as a side effect.
        boolean hasCreates = actions.stream().anyMatch(a -> a.action() == SyncAction.CREATE);
        EverfiCategoryLabel uploadListLabel = hasCreates && !dryRun
                ? loader.loadOrCreateTodaysUploadListLabel()
                : null;

        var results = executor.executeAll(actions, uploadListLabel, dryRun);
        return SyncRun.of(results, dryRun);
    }
}
