package gov.nysenate.ess.core.model.pec;

import com.google.common.collect.ComparisonChain;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * A personnel related task assigned to a specific employee.
 */
public class PersonnelAssignedTask implements Comparable<PersonnelAssignedTask> {

    /** Id of task assignee. */
    private final int empId;
    /** Identifies task to be completed */
    private final PersonnelTaskId taskId;
    /** Time of last action on the task. */
    private final LocalDateTime timestamp;
    /** User that updated the task.  Generally same as employee id. */
    private final Integer updateUserId;
    /** Whether or not the task is fully completed */
    private final boolean completed;

    public PersonnelAssignedTask(int empId,
                                 @Nonnull PersonnelTaskId taskId,
                                 LocalDateTime timestamp,
                                 Integer updateUserId,
                                 boolean completed) {
        this.empId = empId;
        this.taskId = Objects.requireNonNull(taskId);
        this.timestamp = timestamp;
        this.updateUserId = updateUserId;
        this.completed = completed;
    }

    /* --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonnelAssignedTask)) return false;
        PersonnelAssignedTask that = (PersonnelAssignedTask) o;
        return empId == that.empId &&
                completed == that.completed &&
                com.google.common.base.Objects.equal(taskId, that.taskId) &&
                com.google.common.base.Objects.equal(timestamp, that.timestamp) &&
                com.google.common.base.Objects.equal(updateUserId, that.updateUserId);
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(empId, taskId, timestamp, updateUserId, completed);
    }

    @Override
    public int compareTo(PersonnelAssignedTask o) {
        return ComparisonChain.start()
                .compare(empId, o.empId)
                .compare(taskId, o.taskId)
                .result();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PersonnelAssignedTask.class.getSimpleName() + "[", "]")
                .add("empId=" + empId)
                .add("taskId=" + taskId)
                .add("timestamp=" + timestamp)
                .add("updateUserId=" + updateUserId)
                .add("completed=" + completed)
                .toString();
    }

    /* --- Functional Getters --- */

    public PersonnelTaskType getTaskType() {
        return taskId.getTaskType();
    }

    public int getTaskNumber() {
        return taskId.getTaskNumber();
    }

    /* --- Getters --- */

    public int getEmpId() {
        return empId;
    }

    public PersonnelTaskId getTaskId() {
        return taskId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Integer getUpdateUserId() {
        return updateUserId;
    }

    public boolean isCompleted() {
        return completed;
    }
}
