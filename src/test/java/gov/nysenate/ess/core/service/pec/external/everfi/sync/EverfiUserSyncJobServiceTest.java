package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import gov.nysenate.ess.core.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(HierarchicalContextRunner.class)
@Category(UnitTest.class)
public class EverfiUserSyncJobServiceTest {

    @Test
    public void runUserSync_returnsSuccessAndSendsReportEmail() {
        RecordingSyncService syncService = new RecordingSyncService(SyncRun.of(List.of(), true));
        RecordingReportMailer reportMailer = new RecordingReportMailer();
        EverfiUserSyncJobService jobService = new EverfiUserSyncJobService(false, true, syncService, reportMailer);

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
        EverfiUserSyncJobService jobService = new EverfiUserSyncJobService(false, true, syncService, reportMailer);

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
        EverfiUserSyncJobService jobService = new EverfiUserSyncJobService(false, true, syncService, reportMailer);

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
        EverfiUserSyncJobService jobService = new EverfiUserSyncJobService(false, true, syncService, reportMailer);

        EverfiUserSyncJobResult result = jobService.runUserSync(false, false);

        assertThat(result.success()).isFalse();
        assertThat(result.message()).isEqualTo("Everfi user sync finished with 1 error(s). Report email not requested.");
        assertThat(reportMailer.sentRuns).isEmpty();
    }

    @Test
    public void runUserSync_returnsErrorWhenSyncThrows() {
        RecordingSyncService syncService = RecordingSyncService.failing(new RuntimeException("load failed"));
        RecordingReportMailer reportMailer = new RecordingReportMailer();
        EverfiUserSyncJobService jobService = new EverfiUserSyncJobService(false, true, syncService, reportMailer);

        EverfiUserSyncJobResult result = jobService.runUserSync(false, true);

        assertThat(result.success()).isFalse();
        assertThat(result.message()).isEqualTo("Everfi user sync failed before completion: load failed");
        assertThat(reportMailer.sentRuns).isEmpty();
    }

    @Test
    public void runUserSync_returnsErrorWhenReportEmailFails() {
        RecordingSyncService syncService = new RecordingSyncService(SyncRun.of(List.of(), false));
        RecordingReportMailer reportMailer = RecordingReportMailer.failing(new RuntimeException("mail failed"));
        EverfiUserSyncJobService jobService = new EverfiUserSyncJobService(false, true, syncService, reportMailer);

        EverfiUserSyncJobResult result = jobService.runUserSync(false, true);

        assertThat(result.success()).isFalse();
        assertThat(result.message())
                .isEqualTo("Everfi user sync finished successfully, but report email failed: mail failed");
    }

    @Test
    public void runScheduledUserSync_doesNotRunWhenSchedulerDisabled() {
        RecordingSyncService syncService = new RecordingSyncService(SyncRun.of(List.of(), true));
        RecordingReportMailer reportMailer = new RecordingReportMailer();
        EverfiUserSyncJobService jobService = new EverfiUserSyncJobService(false, true, syncService, reportMailer);

        jobService.runScheduledUserSync();

        assertThat(syncService.syncUsersCalls).isEqualTo(0);
        assertThat(reportMailer.sentRuns).isEmpty();
    }

    @Test
    public void runScheduledUserSync_runsWithConfiguredDryRunAndSendsReportEmail() {
        RecordingSyncService syncService = new RecordingSyncService(SyncRun.of(List.of(), false));
        RecordingReportMailer reportMailer = new RecordingReportMailer();
        EverfiUserSyncJobService jobService = new EverfiUserSyncJobService(true, false, syncService, reportMailer);

        jobService.runScheduledUserSync();

        assertThat(syncService.syncUsersCalls).isEqualTo(1);
        assertThat(syncService.receivedDryRun).isFalse();
        assertThat(reportMailer.sentRuns).containsExactly(syncService.runToReturn);
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
            super(null, null, null);
            this.runToReturn = runToReturn;
            this.exceptionToThrow = null;
        }

        private RecordingSyncService(RuntimeException exceptionToThrow) {
            super(null, null, null);
            this.runToReturn = null;
            this.exceptionToThrow = exceptionToThrow;
        }

        private static RecordingSyncService failing(RuntimeException exceptionToThrow) {
            return new RecordingSyncService(exceptionToThrow);
        }

        @Override
        public SyncRun syncUsers(boolean dryRun) {
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
}
