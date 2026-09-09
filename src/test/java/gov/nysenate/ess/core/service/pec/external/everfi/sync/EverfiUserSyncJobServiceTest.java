package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import gov.nysenate.ess.core.annotation.UnitTest;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(HierarchicalContextRunner.class)
@Category(UnitTest.class)
public class EverfiUserSyncJobServiceTest {

    private static TestLoggerControl logControl;

    @BeforeClass
    public static void suppressExpectedErrorLogs() {
        logControl = TestLoggerControl.suppress(EverfiUserSyncJobService.class);
    }

    @AfterClass
    public static void restoreLoggerLevel() {
        logControl.restore();
    }

    @Test
    public void runUserSync_returnsSuccessAndSendsReportEmail() {
        RecordingSyncService syncService = new RecordingSyncService(SyncRun.of(List.of(), true));
        RecordingReportMailer reportMailer = new RecordingReportMailer();
        EverfiUserSyncJobService jobService = createJobService(false, true, syncService, reportMailer);

        EverfiUserSyncJobResult result = jobService.runUserSync(true, true);

        assertThat(result.success()).isTrue();
        assertThat(result.message()).isEqualTo("Everfi user sync finished successfully. Report email sent.");
        assertThat(syncService.receivedDryRun).isTrue();
        assertThat(reportMailer.sentRuns).containsExactly(syncService.runToReturn);
    }

    @Test
    public void runUserSync_skipsReportEmailWhenNotRequested() {
        RecordingSyncService syncService = new RecordingSyncService(SyncRun.of(List.of(), false));
        RecordingReportMailer reportMailer = new RecordingReportMailer();
        EverfiUserSyncJobService jobService = createJobService(false, true, syncService, reportMailer);

        EverfiUserSyncJobResult result = jobService.runUserSync(false, false);

        assertThat(result.success()).isTrue();
        assertThat(result.message()).isEqualTo("Everfi user sync finished successfully. Report email not requested.");
        assertThat(syncService.receivedDryRun).isFalse();
        assertThat(reportMailer.sentRuns).isEmpty();
    }

    @Test
    public void runUserSync_returnsErrorWhenSyncRunHasErrorResultsAndStillSendsReportEmail() {
        PlannedAction action = new PlannedAction(SyncAction.UPDATE, desiredUser(), null, List.of());
        SyncRun run = SyncRun.of(List.of(SyncResult.error(action, "update failed")), false);
        RecordingSyncService syncService = new RecordingSyncService(run);
        RecordingReportMailer reportMailer = new RecordingReportMailer();
        EverfiUserSyncJobService jobService = createJobService(false, true, syncService, reportMailer);

        EverfiUserSyncJobResult result = jobService.runUserSync(false, true);

        assertThat(result.success()).isFalse();
        assertThat(result.message()).isEqualTo("Everfi user sync finished with 1 error(s). Report email sent.");
        assertThat(reportMailer.sentRuns).containsExactly(run);
    }

    @Test
    public void runUserSync_returnsErrorWhenSyncRunHasErrorResultsAndReportEmailNotRequested() {
        PlannedAction action = new PlannedAction(SyncAction.UPDATE, desiredUser(), null, List.of());
        SyncRun run = SyncRun.of(List.of(SyncResult.error(action, "update failed")), false);
        RecordingSyncService syncService = new RecordingSyncService(run);
        RecordingReportMailer reportMailer = new RecordingReportMailer();
        EverfiUserSyncJobService jobService = createJobService(false, true, syncService, reportMailer);

        EverfiUserSyncJobResult result = jobService.runUserSync(false, false);

        assertThat(result.success()).isFalse();
        assertThat(result.message()).isEqualTo("Everfi user sync finished with 1 error(s). Report email not requested.");
        assertThat(reportMailer.sentRuns).isEmpty();
    }

    @Test
    public void runUserSync_returnsErrorWhenSyncThrows() {
        RecordingSyncService syncService = RecordingSyncService.failing(new RuntimeException("load failed"));
        RecordingReportMailer reportMailer = new RecordingReportMailer();
        EverfiUserSyncJobService jobService = createJobService(false, true, syncService, reportMailer);

        EverfiUserSyncJobResult result = jobService.runUserSync(false, true);

        assertThat(result.success()).isFalse();
        assertThat(result.message()).isEqualTo("Everfi user sync failed before completion: load failed");
        assertThat(reportMailer.sentRuns).isEmpty();
    }

    @Test
    public void runUserSync_returnsErrorWhenReportEmailFails() {
        RecordingSyncService syncService = new RecordingSyncService(SyncRun.of(List.of(), false));
        RecordingReportMailer reportMailer = RecordingReportMailer.failing(new RuntimeException("mail failed"));
        EverfiUserSyncJobService jobService = createJobService(false, true, syncService, reportMailer);

        EverfiUserSyncJobResult result = jobService.runUserSync(false, true);

        assertThat(result.success()).isFalse();
        assertThat(result.message())
                .isEqualTo("Everfi user sync finished successfully, but report email failed: mail failed");
    }

    @Test
    public void runScheduledUserSync_doesNotRunWhenSchedulerDisabled() {
        RecordingSyncService syncService = new RecordingSyncService(SyncRun.of(List.of(), true));
        RecordingReportMailer reportMailer = new RecordingReportMailer();
        EverfiUserSyncJobService jobService = createJobService(false, true, syncService, reportMailer);

        jobService.runScheduledUserSync();

        assertThat(syncService.syncUsersCalls).isEqualTo(0);
        assertThat(reportMailer.sentRuns).isEmpty();
    }

    @Test
    public void runScheduledUserSync_runsWithConfiguredDryRunAndSendsReportEmail() {
        RecordingSyncService syncService = new RecordingSyncService(SyncRun.of(List.of(), false));
        RecordingReportMailer reportMailer = new RecordingReportMailer();
        EverfiUserSyncJobService jobService = createJobService(true, false, syncService, reportMailer);

        jobService.runScheduledUserSync();

        assertThat(syncService.syncUsersCalls).isEqualTo(1);
        assertThat(syncService.receivedDryRun).isFalse();
        assertThat(reportMailer.sentRuns).containsExactly(syncService.runToReturn);
    }

    @Test
    public void failedRunDoesNotResetStaleAlertCooldown() {
        MutableClock clock = new MutableClock(Instant.parse("2026-01-01T00:00:00Z"));
        RecordingSyncService syncService = RecordingSyncService.failing(new RuntimeException("load failed"));
        EverfiUserSyncJobService jobService = new EverfiUserSyncJobService(
                true, false, syncService, new RecordingReportMailer(), 1_000, 2_000, 1_000, clock);

        clock.advance(Duration.ofMillis(2_001));
        assertThat(jobService.healthAlert(clock.instant())).isPresent();

        jobService.runUserSync(false, false);
        clock.advance(Duration.ofMillis(500));

        assertThat(jobService.healthAlert(clock.instant())).isEmpty();

        clock.advance(Duration.ofMillis(500));
        assertThat(jobService.healthAlert(clock.instant()))
                .contains("No successful EverfiUserSync has completed since 2026-01-01T00:00:00Z.");
    }

    @Test
    public void successfulSyncResetsStaleTimerEvenWhenReportEmailFails() {
        MutableClock clock = new MutableClock(Instant.parse("2026-01-01T00:00:00Z"));
        EverfiUserSyncJobService jobService = new EverfiUserSyncJobService(
                true, false,
                new RecordingSyncService(SyncRun.of(List.of(), false)),
                RecordingReportMailer.failing(new RuntimeException("mail failed")),
                1_000, 2_000, 1_000, clock);

        clock.advance(Duration.ofMillis(1_500));
        EverfiUserSyncJobResult result = jobService.runUserSync(false, true);
        clock.advance(Duration.ofMillis(1_000));

        assertThat(result.success()).isFalse();
        assertThat(jobService.healthAlert(clock.instant())).isEmpty();
    }

    @Test
    public void concurrentRunIsRejectedAndActiveRunRemainsObservable() throws Exception {
        MutableClock clock = new MutableClock(Instant.parse("2026-01-01T00:00:00Z"));
        BlockingSyncService syncService = new BlockingSyncService();
        EverfiUserSyncJobService jobService = new EverfiUserSyncJobService(
                true, false, syncService, new RecordingReportMailer(),
                1_000, 2_000, 1_000, clock);
        FutureTask<EverfiUserSyncJobResult> activeRun =
                new FutureTask<>(() -> jobService.runUserSync(false, false));
        Thread activeRunThread = new Thread(activeRun, "everfi-user-sync-test");

        activeRunThread.start();
        try {
            assertThat(syncService.entered.await(1, TimeUnit.SECONDS)).isTrue();
            clock.advance(Duration.ofMillis(1_001));

            assertThat(jobService.healthAlert(clock.instant()))
                    .hasValueSatisfying(message -> assertThat(message).contains("has been running since"));
            assertThat(jobService.runUserSync(false, false))
                    .isEqualTo(EverfiUserSyncJobResult.error("An Everfi user sync is already in progress."));
            assertThat(syncService.syncUsersCalls).isEqualTo(1);
        } finally {
            syncService.release.countDown();
        }

        assertThat(activeRun.get(1, TimeUnit.SECONDS).success()).isTrue();
    }

    @Test
    public void watchdogDurationsMustBePositive() {
        Throwable exception = catchThrowable(() ->
                new EverfiUserSyncJobService(true, false,
                        new RecordingSyncService(SyncRun.of(List.of(), false)),
                        new RecordingReportMailer(), 0, 1, 1, Clock.systemUTC()));

        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(exception).hasMessage("everfi.user.sync.max-runtime-ms must be positive.");
    }

    private EverfiUserSyncJobService createJobService(boolean syncEnabled,
                                                       boolean dryRunEnabled,
                                                       EverfiUserSyncService syncService,
                                                       SyncReportMailer reportMailer) {
        long testWatchdogDurationMs = Duration.ofMinutes(1).toMillis();
        return new EverfiUserSyncJobService(syncEnabled, dryRunEnabled, syncService, reportMailer,
                testWatchdogDurationMs, testWatchdogDurationMs, testWatchdogDurationMs, Clock.systemUTC());
    }

    private DesiredUser desiredUser() {
        return DesiredUser.builder()
                .employeeId(1)
                .email("user@nysenate.gov")
                .firstName("Test")
                .lastName("User")
                .build();
    }

    private static class RecordingSyncService extends EverfiUserSyncService {
        private final SyncRun runToReturn;
        private final RuntimeException exceptionToThrow;
        private boolean receivedDryRun;
        private int syncUsersCalls;

        private RecordingSyncService(SyncRun runToReturn) {
            super(null, null, null, null, null, null);
            this.runToReturn = runToReturn;
            this.exceptionToThrow = null;
        }

        private RecordingSyncService(RuntimeException exceptionToThrow) {
            super(null, null, null, null, null, null);
            this.runToReturn = null;
            this.exceptionToThrow = exceptionToThrow;
        }

        private static RecordingSyncService failing(RuntimeException exceptionToThrow) {
            return new RecordingSyncService(exceptionToThrow);
        }

        @Override
        SyncRun syncUsers(boolean dryRun) {
            syncUsersCalls++;
            this.receivedDryRun = dryRun;
            if (exceptionToThrow != null) {
                throw exceptionToThrow;
            }
            return runToReturn;
        }
    }

    private static class RecordingReportMailer extends SyncReportMailer {
        private final RuntimeException exceptionToThrow;
        private final List<SyncRun> sentRuns = new java.util.ArrayList<>();

        private RecordingReportMailer() {
            super("pec-admin@nysenate.gov", null);
            this.exceptionToThrow = null;
        }

        private RecordingReportMailer(RuntimeException exceptionToThrow) {
            super("pec-admin@nysenate.gov", null);
            this.exceptionToThrow = exceptionToThrow;
        }

        private static RecordingReportMailer failing(RuntimeException exceptionToThrow) {
            return new RecordingReportMailer(exceptionToThrow);
        }

        @Override
        void sendSyncRunToPecAdmin(SyncRun run) {
            if (exceptionToThrow != null) {
                throw exceptionToThrow;
            }
            sentRuns.add(run);
        }
    }

    private static class BlockingSyncService extends EverfiUserSyncService {
        private final CountDownLatch entered = new CountDownLatch(1);
        private final CountDownLatch release = new CountDownLatch(1);
        private volatile int syncUsersCalls;

        private BlockingSyncService() {
            super(null, null, null, null, null, null);
        }

        @Override
        SyncRun syncUsers(boolean dryRun) {
            syncUsersCalls++;
            entered.countDown();
            try {
                if (!release.await(1, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("Timed out waiting to release test sync.");
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(ex);
            }
            return SyncRun.of(List.of(), dryRun);
        }
    }

    private static class MutableClock extends Clock {
        private Instant instant;

        private MutableClock(Instant instant) {
            this.instant = instant;
        }

        private void advance(Duration duration) {
            instant = instant.plus(duration);
        }

        @Override
        public ZoneId getZone() {
            return ZoneId.of("UTC");
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }
}
