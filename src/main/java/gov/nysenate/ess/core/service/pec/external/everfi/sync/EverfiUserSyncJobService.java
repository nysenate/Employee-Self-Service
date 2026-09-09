package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Scheduled entrypoint for the sync. Runs once daily when {@code scheduler.everfi.sync.enabled}
 * is true and emails the resulting report to PEC admins.
 *
 * <p>This service is the serialized entrypoint for sync execution. Manual and scheduled runs must
 * both come through {@link #runUserSync(boolean, boolean)}. A concurrent invocation is rejected so
 * request and scheduler threads do not wait behind a stalled run.
 */
@Service
public class EverfiUserSyncJobService {

    private static final Logger logger = LoggerFactory.getLogger(EverfiUserSyncJobService.class);

    private final boolean everfiSyncEnabled;
    private final boolean dryRunEnabled;
    private final EverfiUserSyncService everfiUserSyncService;
    private final SyncReportMailer reportMailer;
    private final Clock clock;
    private final Duration maxRunDuration;
    private final Duration staleSuccessDuration;
    private final Duration watchdogAlertRepeat;
    private final Instant applicationStartedAt;
    private final Object healthStateLock = new Object();

    private Instant activeRunStartedAt;
    private Instant lastSuccessfulSyncAt;
    private Instant lastLongRunAlertAt;
    private Instant lastStaleSuccessAlertAt;

    @Autowired
    public EverfiUserSyncJobService(
            @Value("${scheduler.everfi.sync.enabled:false}") boolean everfiSyncEnabled,
            @Value("${everfi.user.sync.dry-run.enabled:true}") boolean dryRunEnabled,
            EverfiUserSyncService everfiUserSyncService,
            SyncReportMailer reportMailer,
            @Value("${everfi.user.sync.max-runtime-ms:1800000}") long maxRuntimeMs,
            @Value("${everfi.user.sync.stale-success-ms:129600000}") long staleSuccessMs,
            @Value("${everfi.user.sync.watchdog.alert-repeat-ms:3600000}") long watchdogAlertRepeatMs
    ) {
        this(everfiSyncEnabled, dryRunEnabled, everfiUserSyncService, reportMailer,
                maxRuntimeMs, staleSuccessMs, watchdogAlertRepeatMs, Clock.systemUTC());
    }

    EverfiUserSyncJobService(boolean everfiSyncEnabled,
                             boolean dryRunEnabled,
                             EverfiUserSyncService everfiUserSyncService,
                             SyncReportMailer reportMailer,
                             long maxRuntimeMs,
                             long staleSuccessMs,
                             long watchdogAlertRepeatMs,
                             Clock clock) {
        this.everfiSyncEnabled = everfiSyncEnabled;
        this.dryRunEnabled = dryRunEnabled;
        this.everfiUserSyncService = everfiUserSyncService;
        this.reportMailer = reportMailer;
        this.clock = Objects.requireNonNull(clock);
        this.maxRunDuration = positiveDuration("everfi.user.sync.max-runtime-ms", maxRuntimeMs);
        this.staleSuccessDuration = positiveDuration("everfi.user.sync.stale-success-ms", staleSuccessMs);
        this.watchdogAlertRepeat = positiveDuration(
                "everfi.user.sync.watchdog.alert-repeat-ms", watchdogAlertRepeatMs);
        this.applicationStartedAt = clock.instant();
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
    public EverfiUserSyncJobResult runUserSync(boolean dryRun, boolean sendReportEmail) {
        if (!markRunStarted(clock.instant())) {
            return EverfiUserSyncJobResult.error("An Everfi user sync is already in progress.");
        }

        long startNanos = System.nanoTime();
        try {
            SyncRun run = everfiUserSyncService.syncUsers(dryRun);
            int errorCount = countErrors(run);
            if (errorCount == 0) {
                markSyncSuccessful(clock.instant());
            }

            try {
                if (sendReportEmail) {
                    logger.info("Starting EverfiUserSync report email delivery.");
                    reportMailer.sendSyncRunToPecAdmin(run);
                    logger.info("Finished EverfiUserSync report email delivery.");
                }
            } catch (RuntimeException ex) {
                logger.error("Failed to email EverfiUserSync report.", ex);
                return EverfiUserSyncJobResult.error(
                        reportEmailFailureMessage(errorCount, ex)
                );
            }

            if (errorCount > 0) {
                logger.error("EverfiUserSync completed with {} action error(s).", errorCount);
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
        } finally {
            markRunFinished();
            logger.info("EverfiUserSync invocation completed in {} ms.",
                    (System.nanoTime() - startNanos) / 1_000_000);
        }
    }

    @Scheduled(fixedDelayString = "${everfi.user.sync.watchdog.interval-ms:60000}")
    public void monitorUserSyncHealth() {
        healthAlert(clock.instant()).ifPresent(logger::error);
    }

    Optional<String> healthAlert(Instant now) {
        if (!everfiSyncEnabled) {
            return Optional.empty();
        }

        synchronized (healthStateLock) {
            Instant activeSince = activeRunStartedAt;
            if (activeSince != null && Duration.between(activeSince, now).compareTo(maxRunDuration) > 0) {
                if (isAlertDue(lastLongRunAlertAt, now)) {
                    lastLongRunAlertAt = now;
                    return Optional.of("EverfiUserSync has been running since " + activeSince
                            + " (maximum expected runtime is " + maxRunDuration.toMinutes() + " minutes).");
                }
                return Optional.empty();
            }

            Instant successBaseline = lastSuccessfulSyncAt != null ? lastSuccessfulSyncAt : applicationStartedAt;
            if (activeSince == null && Duration.between(successBaseline, now).compareTo(staleSuccessDuration) > 0) {
                if (isAlertDue(lastStaleSuccessAlertAt, now)) {
                    lastStaleSuccessAlertAt = now;
                    return Optional.of("No successful EverfiUserSync has completed since " + successBaseline + ".");
                }
            }
            return Optional.empty();
        }
    }

    private boolean markRunStarted(Instant startedAt) {
        synchronized (healthStateLock) {
            if (activeRunStartedAt != null) {
                return false;
            }
            activeRunStartedAt = startedAt;
            lastLongRunAlertAt = null;
            return true;
        }
    }

    private void markSyncSuccessful(Instant completedAt) {
        synchronized (healthStateLock) {
            lastSuccessfulSyncAt = completedAt;
            lastStaleSuccessAlertAt = null;
        }
        logger.info("Recorded successful EverfiUserSync completion at {}.", completedAt);
    }

    private void markRunFinished() {
        synchronized (healthStateLock) {
            activeRunStartedAt = null;
        }
    }

    private boolean isAlertDue(Instant previousAlert, Instant now) {
        return previousAlert == null
                || Duration.between(previousAlert, now).compareTo(watchdogAlertRepeat) >= 0;
    }

    private static Duration positiveDuration(String propertyName, long millis) {
        if (millis <= 0) {
            throw new IllegalArgumentException(propertyName + " must be positive.");
        }
        return Duration.ofMillis(millis);
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
