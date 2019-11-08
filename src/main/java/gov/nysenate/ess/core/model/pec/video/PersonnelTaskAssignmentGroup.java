package gov.nysenate.ess.core.model.pec.video;

/**
 * Classification which dictates assignment logic for a task.
 */
public enum PersonnelTaskAssignmentGroup {
    /** Default logic.  Assigns any unassigned active tasks. */
    DEFAULT,
    /** Supports assignment of ethics tasks, which may fulfill equivalent requirements. */
    ETHICS,
}
