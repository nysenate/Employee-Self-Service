package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.function.Supplier;

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

    private final EverfiCategoryService categoryService;
    private final EverfiUserSyncLoader loader;
    private final EverfiUserSyncPlanner planner;
    private final EverfiLabelProvisioner labelProvisioner;
    private final EverfiExecutableActionResolver actionResolver;
    private final EverfiUserSyncExecutor executor;

    public EverfiUserSyncService(
            EverfiCategoryService categoryService,
            EverfiUserSyncLoader loader,
            EverfiUserSyncPlanner planner,
            EverfiLabelProvisioner labelProvisioner,
            EverfiExecutableActionResolver actionResolver,
            EverfiUserSyncExecutor executor
    ) {
        this.categoryService = categoryService;
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
        runStage("initialize category cache", this::initializeCategoryCache);
        var desiredUsers = runStage("load desired users", loader::loadDesiredUsers);
        var remoteLoadResult = runStage("load remote users", loader::loadRemoteUsers);
        var actions = runStage("plan actions",
                () -> planner.plan(desiredUsers, RemoteUserIndex.from(remoteLoadResult)));
        var labelRequirements = LabelRequirements.from(actions);
        var labels = runStage("resolve labels",
                () -> labelProvisioner.resolve(labelRequirements, dryRun));
        var executableActions = runStage("resolve executable actions",
                () -> actionResolver.resolve(actions, labels));
        var results = runStage("execute actions",
                () -> executor.executeAll(executableActions, dryRun));
        return runStage("build sync report", () -> SyncRun.of(results, dryRun));
    }

    private <T> T runStage(String stageName, Supplier<T> stage) {
        long startNanos = System.nanoTime();
        logger.info("Starting Everfi user sync stage: {}.", stageName);
        try {
            T result = stage.get();
            long durationMs = (System.nanoTime() - startNanos) / 1_000_000;
            logger.info("Finished Everfi user sync stage: {} ({} ms).", stageName, durationMs);
            return result;
        } catch (RuntimeException ex) {
            long durationMs = (System.nanoTime() - startNanos) / 1_000_000;
            logger.error("Failed Everfi user sync stage: {} ({} ms).", stageName, durationMs, ex);
            throw ex;
        }
    }

    private void runStage(String stageName, Runnable stage) {
        runStage(stageName, () -> {
            stage.run();
            return null;
        });
    }

    void initializeCategoryCache() {
        try {
            categoryService.initialize();
        } catch (IOException | RuntimeException ex) {
            throw new EverfiUserSyncLoadException("Failed to initialize Everfi category cache.", ex);
        }
    }
}
