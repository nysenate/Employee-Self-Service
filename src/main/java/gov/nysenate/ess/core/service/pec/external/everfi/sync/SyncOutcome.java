package gov.nysenate.ess.core.service.pec.external.everfi.sync;

/**
 * The runtime fate of a planned action. SKIP and FLAG actions always produce SKIPPED/FLAGGED;
 * other actions produce SUCCESS or ERROR depending on the executor outcome.
 */
public enum SyncOutcome {
    SUCCESS,
    SKIPPED,
    FLAGGED,
    ERROR
}
