package gov.nysenate.ess.core.model.pec;

/**
 * Interface representing a personnel task.
 */
public interface PersonnelTask {

    /** Get a unique identifier for the task */
    PersonnelTaskId getTaskId();

    /** Get the task type */
    default PersonnelTaskType getTaskType() {
        return getTaskId().getTaskType();
    }

    /** Get a title describing the task */
    String getTitle();
}
