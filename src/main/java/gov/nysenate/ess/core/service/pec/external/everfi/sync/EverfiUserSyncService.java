package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategorySnapshot;
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

    private final EverfiUserSyncPreflight preflight;
    private final EverfiUserSyncPlanner planner;
    private final EverfiUserSyncExecutor executor;

    public EverfiUserSyncService(
            EverfiUserSyncPreflight preflight,
            EverfiUserSyncPlanner planner,
            EverfiUserSyncExecutor executor
    ) {
        this.preflight = preflight;
        this.planner = planner;
        this.executor = executor;
    }

    /**
     * Runs the pipeline once. When {@code dryRun} is true, no remote or local writes occur, but
     * the returned {@link SyncRun} still reflects what would have happened — including any errors
     * raised during the load and plan stages.
     */
    SyncRun syncUsers(boolean dryRun) {
        EverfiCategorySnapshot snapshot = preflight.loadCategorySnapshot();
        if (!dryRun) {
            if (preflight.ensureDepartmentLabels(snapshot)) {
                // Bootstrap created new labels; refetch so downstream sees them.
                snapshot = preflight.loadCategorySnapshot();
            }
        }

        var desiredUsers = preflight.loadDesiredUsers(snapshot);
        var remoteLoadResult = preflight.loadRemoteUsers(snapshot);

        var actions = planner.plan(desiredUsers, RemoteUserIndex.from(remoteLoadResult));

        // Only find/create the Upload List label if there are actually users to create,
        // and only on a live run — dry runs must not create labels as a side effect.
        boolean hasCreates = actions.stream().anyMatch(a -> a.action() == SyncAction.CREATE);
        EverfiCategoryLabel uploadListLabel = hasCreates && !dryRun
                ? preflight.ensureTodaysUploadListLabel(snapshot)
                : null;

        var results = executor.executeAll(actions, uploadListLabel, dryRun);
        return SyncRun.of(results, dryRun);
    }
}
