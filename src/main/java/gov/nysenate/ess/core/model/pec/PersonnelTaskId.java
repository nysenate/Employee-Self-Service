package gov.nysenate.ess.core.model.pec;

import com.google.common.collect.ComparisonChain;

import javax.annotation.Nonnull;
import java.util.Objects;

public class PersonnelTaskId implements Comparable<PersonnelTaskId> {

    /** The type of task to be completed */
    private PersonnelTaskType taskType;

    /**
     * Identifies the specific task within the context of the type.
     * Come types consist of only one task, and will have a null taskNumber.
     */
    private Integer taskNumber;

    public PersonnelTaskId(@Nonnull PersonnelTaskType taskType, Integer taskNumber) {
        this.taskType = Objects.requireNonNull(taskType);
        this.taskNumber = taskNumber;
    }

    /* --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonnelTaskId)) return false;
        PersonnelTaskId that = (PersonnelTaskId) o;
        return taskType == that.taskType &&
                com.google.common.base.Objects.equal(taskNumber, that.taskNumber);
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(taskType, taskNumber);
    }

    @Override
    public int compareTo(PersonnelTaskId o) {
        return ComparisonChain.start()
                .compare(taskType, o.taskType)
                .compare(taskNumber, o.taskNumber)
                .result();
    }

    @Override
    public String toString() {
        String str = taskType.name();
        if (taskNumber != null) {
            str += "#" + taskNumber;
        }
        return str;
    }

    /* --- Getters --- */

    public PersonnelTaskType getTaskType() {
        return taskType;
    }

    public Integer getTaskNumber() {
        return taskNumber;
    }
}
