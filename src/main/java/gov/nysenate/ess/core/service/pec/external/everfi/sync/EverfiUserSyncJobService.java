package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Scheduled entrypoint for the sync. Runs once daily when {@code scheduler.everfi.sync.enabled}
 * is true and emails the resulting report to PEC admins.
 */
@Service
public class EverfiUserSyncJobService {

    private static final Logger logger = LoggerFactory.getLogger(EverfiUserSyncJobService.class);

    private boolean everfiSyncEnabled;
    private final EverfiUserSyncService everfiUserSyncService;
    private final EverfiUserSyncReportService reportService;

    public EverfiUserSyncJobService(
            @Value("${scheduler.everfi.sync.enabled:false}") boolean everfiSyncEnabled,
            EverfiUserSyncService everfiUserSyncService,
            EverfiUserSyncReportService reportService
    ) {
        this.everfiSyncEnabled = everfiSyncEnabled;
        this.everfiUserSyncService = everfiUserSyncService;
        this.reportService = reportService;
    }

    @Scheduled(cron = "0 44 22 * * *")
    public void runScheduledUserSync() {
        if (!everfiSyncEnabled) {
            logger.info("EverfiUserSyncJobService is disabled and exiting run.");
            return;
        }
        logger.info("Executing a scheduled EverfiUserSync run.");
        var dryRun = true;
        SyncRun run = everfiUserSyncService.syncUsers(dryRun);
        logger.info("Finished scheduled EverfiUserSync run.");
        reportService.sendSyncRunToPecAdmin(run);
    }
}
