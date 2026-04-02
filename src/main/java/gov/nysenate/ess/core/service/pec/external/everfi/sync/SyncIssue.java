package gov.nysenate.ess.core.service.pec.external.everfi.sync;

public enum SyncIssue {

    /**
     * Multiple Everfi users share the same employee_id in the remote snapshot.
     */
    DUPLICATE_REMOTE_EMP_ID,

    /**
     * A mapping row exists but no corresponding user was found in the remote snapshot.
     */
    MAPPING_WITHOUT_REMOTE_USER,

    /**
     * A user exists in the remote snapshot with a matching employee_id
     * but has no entry in the mapping table.
     */
    UNMAPPED_REMOTE_USER,

    /**
     * The employee has no email address, which is required for Everfi user creation.
     */
    MISSING_EMAIL,

    /**
     * An active remote user exists with no mapping and no corresponding desired user —
     * it may have been created outside of this sync process. Requires human review.
     */
    UNRECOGNIZED_ACTIVE_REMOTE,

    /**
     * The employee ID on the remote user does not match the employee ID recorded in its mapping —
     * indicating out-of-band modification or data corruption. Requires human review.
     */
    MAPPING_EMPLOYEE_ID_MISMATCH,
}
