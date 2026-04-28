package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Final output of a single sync run — every {@link SyncResult} produced, plus the mode and timestamp.
 * Consumed by {@link SyncReportRenderer} to render the admin email.
 */
public record SyncRun(List<SyncResult> results, boolean dryRun, LocalDateTime ranAt) {

    public static SyncRun of(List<SyncResult> results, boolean dryRun) {
        return new SyncRun(List.copyOf(results), dryRun, LocalDateTime.now());
    }
}
