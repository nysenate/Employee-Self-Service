package gov.nysenate.ess.core.dao.pec.assignment;

import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;

/**
 * Enumerates choices for filtering results based on completion status in {@link PersonnelTaskAssignment} queries.
 */
public enum PTAQueryCompletionStatus {
    ALL_INCOMPLETE(true, false),
    SOME_INCOMPLETE(false, false),
    SOME_COMPLETE(false, true),
    ALL_COMPLETE(true, true),
    ;

    PTAQueryCompletionStatus(boolean all, boolean completed) {
        this.all = all;
        this.completed = completed;
    }

    /** Requires match for all results if true, at least one match if false */
    private boolean all;
    /** Match complete tasks if true, incomplete otherwise */
    private boolean completed;

    public boolean isAll() {
        return all;
    }

    public boolean isCompleted() {
        return completed;
    }
}
