package gov.nysenate.ess.core.service.pec.external.everfi.sync;

public enum SyncAction {

    /**
     * No mapping exists for this employee. A new Everfi user should be created.
     */
    CREATE,

    /**
     * A mapping exists and the remote user is inactive. The user should be reactivated.
     */
    REACTIVATE,

    /**
     * A mapping exists, the remote user is active, and local fields differ from remote.
     */
    UPDATE,

    /**
     * A mapping exists and the remote user is active, but the employee is no longer active locally.
     */
    DEACTIVATE,

    /**
     * No action required. Remote state matches desired state.
     */
    SKIP,

    /**
     * Action cannot be determined or safely executed. Requires human review.
     * See attached SyncIssues for detail.
     */
    FLAG
}
