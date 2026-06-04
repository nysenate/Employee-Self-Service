package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Scheduled entrypoint for the sync. Runs once daily when {@code scheduler.everfi.sync.enabled}
 * is true and emails the resulting report to PEC admins.
 *
 * <p>This service is the serialized entrypoint for sync execution. Manual and scheduled runs must
 * both come through {@link #runUserSync(boolean, boolean)} so the synchronized keyword applies.
 */
@Service
public class EverfiUserSyncJobService {

    private static final Logger logger = LoggerFactory.getLogger(EverfiUserSyncJobService.class);

    private final boolean everfiSyncEnabled;
    private final boolean dryRunEnabled;
    private final EverfiUserSyncService everfiUserSyncService;
    private final SyncReportMailer reportMailer;

    public EverfiUserSyncJobService(
            @Value("${scheduler.everfi.sync.enabled:false}") boolean everfiSyncEnabled,
            @Value("${everfi.user.sync.dry-run.enabled:true}") boolean dryRunEnabled,
            EverfiUserSyncService everfiUserSyncService,
            SyncReportMailer reportMailer
    ) {
        this.everfiSyncEnabled = everfiSyncEnabled;
        this.dryRunEnabled = dryRunEnabled;
        this.everfiUserSyncService = everfiUserSyncService;
        this.reportMailer = reportMailer;
    }

    @Scheduled(cron = "${scheduler.everfi.user.update.cron:0 0 23 * * *}")
    public void runScheduledUserSync() {
        if (!everfiSyncEnabled) {
            logger.info("EverfiUserSyncJobService is disabled and exiting run.");
            return;
        }
        logger.info("Executing a scheduled EverfiUserSync run.");
        EverfiUserSyncJobResult result = runUserSync(dryRunEnabled, true);
        logger.info("Finished scheduled EverfiUserSync run: {}", result.message());
    }

    /**
     * Runs the Everfi user sync once and optionally emails the detailed report to PEC admins.
     * The returned result is intentionally small because the report email is the detailed audit trail.
     */
    public synchronized EverfiUserSyncJobResult runUserSync(boolean dryRun, boolean sendReportEmail) {
        try {
            SyncRun run = everfiUserSyncService.syncUsers(dryRun);
            int errorCount = countErrors(run);

            try {
                if (sendReportEmail) {
                    reportMailer.sendSyncRunToPecAdmin(run);
                }
            } catch (RuntimeException ex) {
                logger.error("Failed to email EverfiUserSync report.", ex);
                return EverfiUserSyncJobResult.error(
                        reportEmailFailureMessage(errorCount, ex)
                );
            }

            if (errorCount > 0) {
                return EverfiUserSyncJobResult.error(
                        "Everfi user sync finished with " + errorCount + " error(s). " + reportEmailStatus(sendReportEmail)
                );
            }
            return EverfiUserSyncJobResult.success(
                    "Everfi user sync finished successfully. " + reportEmailStatus(sendReportEmail)
            );
        } catch (RuntimeException ex) {
            logger.error("EverfiUserSync run failed before completion.", ex);
            return EverfiUserSyncJobResult.error(
                    "Everfi user sync failed before completion: " + ex.getMessage()
            );
        }
    }

    private int countErrors(SyncRun run) {
        return (int) run.results().stream()
                .filter(result -> result.outcome() == SyncOutcome.ERROR)
                .count();
    }

    private String reportEmailStatus(boolean sendReportEmail) {
        return sendReportEmail ? "Report email sent." : "Report email not requested.";
    }

    private String reportEmailFailureMessage(int syncErrorCount, RuntimeException ex) {
        String syncStatus = syncErrorCount > 0
                ? "finished with " + syncErrorCount + " error(s)"
                : "finished successfully";
        return "Everfi user sync " + syncStatus + ", but report email failed: " + ex.getMessage();
    }
}
