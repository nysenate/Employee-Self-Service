package gov.nysenate.ess.core.model.pec;

import java.util.Comparator;

/**
 * Interface representing a personnel task.
 */
public interface PersonnelTask extends Comparable<PersonnelTask> {

    Comparator<PersonnelTask> personnelTaskComparator = Comparator.comparing(PersonnelTask::getTaskId);

    /** Get a unique identifier for the task */
    PersonnelTaskId getTaskId();

    /** Whether or not this task is currently active */
    boolean isActive();

    /** Get the task type */
    default PersonnelTaskType getTaskType() {
        return getTaskId().getTaskType();
    }

    /** Get a title describing the task */
    String getTitle();

    @Override
    default int compareTo(PersonnelTask o) {
        return personnelTaskComparator.compare(this, o);
    }
}
