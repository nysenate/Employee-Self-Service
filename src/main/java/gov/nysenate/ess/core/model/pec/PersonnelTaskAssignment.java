package gov.nysenate.ess.core.model.pec;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import gov.nysenate.ess.core.util.DateUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.StringJoiner;

/**
 * An assignment of a personnel task to an employee.
 *
 * Contains reference to the task and the current completion status.
 */
public class PersonnelTaskAssignment implements Comparable<PersonnelTaskAssignment> {

    /** Identifies task to be completed */
    private final int taskId;
    /** Id of task assignee. */
    private final int empId;
    /** User that updated the task.  Generally same as employee id. */
    private final Integer updateEmpId;
    /** Time of last action on the task. */
    private final LocalDateTime updateTime;
    private final boolean completed;
    private final boolean active;

    private final boolean manual_override;

    private final LocalDateTime assignmentDate;
    private final LocalDateTime dueDate;


    public PersonnelTaskAssignment(int taskId,
                                   int empId,
                                   Integer updateEmpId,
                                   LocalDateTime updateTime,
                                   boolean completed,
                                   boolean active,
                                   LocalDateTime assignmentDate,
                                   LocalDateTime dueDate) {
        this.taskId = taskId;
        this.empId = empId;
        this.updateEmpId = updateEmpId;
        this.updateTime = updateTime;
        this.completed = completed;
        this.active = active;
        this.manual_override = false;
        this.assignmentDate = assignmentDate;
        this.dueDate = dueDate;
    }

    public PersonnelTaskAssignment(int taskId,
                                   int empId,
                                   Integer updateEmpId,
                                   LocalDateTime updateTime,
                                   boolean completed,
                                   boolean active,
                                   boolean manual_override,
                                   LocalDateTime assignmentDate,
                                   LocalDateTime dueDate) {
        this.taskId = taskId;
        this.empId = empId;
        this.updateEmpId = updateEmpId;
        this.updateTime = updateTime;
        this.completed = completed;
        this.active = active;
        this.manual_override = manual_override;
        this.assignmentDate = assignmentDate;
        this.dueDate = dueDate;
    }

    public static PersonnelTaskAssignment newTask(int empId, int taskId) {
        return new PersonnelTaskAssignment(taskId, empId, null, null, false, true, false, null, null);
    }

    /* --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonnelTaskAssignment)) return false;
        PersonnelTaskAssignment that = (PersonnelTaskAssignment) o;
        return taskId == that.taskId &&
                empId == that.empId &&
                Objects.equal(updateEmpId, that.updateEmpId) &&
                completed == that.completed &&
                active == that.active &&
                Objects.equal(updateTime, that.updateTime) &&
                manual_override == that.manual_override;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(taskId, empId, updateEmpId, updateTime, completed, active);
    }

    @Override
    public int compareTo(PersonnelTaskAssignment o) {
        return ComparisonChain.start()
                .compare(this.empId, o.empId)
                .compare(this.taskId, o.taskId)
                .result();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PersonnelTaskAssignment.class.getSimpleName() + "[", "]")
                .add("taskId=" + taskId)
                .add("empId=" + empId)
                .add("updateEmpId=" + updateEmpId)
                .add("updateTime=" + updateTime)
                .add("completed=" + completed)
                .add("active=" + active)
                .add("manual_override=" + manual_override)
                .add("assignmentDate=" + assignmentDate)
                .add("dueDate=" + dueDate)
                .toString();
    }

    /* --- Getters --- */

    public int getTaskId() {
        return taskId;
    }

    public int getEmpId() {
        return empId;
    }

    public Integer getUpdateEmpId() {
        return updateEmpId;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isActive() {
        return active;
    }

    public boolean wasManuallyOverridden() {
        return manual_override;
    }

    public LocalDateTime getAssignmentDate() {
        return assignmentDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    /**
     * Returns a new PersonnelTaskAssignment that's a copy of this one,
     * but with assignment and due date added.
     * @param continuousServiceDate of the related employee.
     * @param type of the related task.
     * @param isNew if this is a newly created assignment.
     * @return a new PersonnelTaskAssignment with the proper dates.
     */
    public PersonnelTaskAssignment withDates(LocalDate continuousServiceDate, PersonnelTaskType type, boolean isNew, boolean hasCompletedAnEthicsLiveTraining) {
        LocalDateTime assignmentDateTime = (isNew ? LocalDate.now() : continuousServiceDate).atStartOfDay();
        LocalDate dueDate = getDueDate(continuousServiceDate, type, hasCompletedAnEthicsLiveTraining);
        return new PersonnelTaskAssignment(
                getTaskId(), getEmpId(), getUpdateEmpId(),
                getUpdateTime(), isCompleted(), isActive(),
                assignmentDateTime, dueDate == null ? null : dueDate.atStartOfDay());
    }

    public LocalDate getDueDate(LocalDate continuousServiceDate, PersonnelTaskType type, boolean hasCompletedAnEthicsLiveTraining) {
        LocalDate dueDate = null;
        if (type == PersonnelTaskType.MOODLE_COURSE) {
            dueDate = DateUtils.addDays(continuousServiceDate,30);
        } else if (type == PersonnelTaskType.ETHICS_LIVE_COURSE) {
            LocalDate ninetyDaysAgo = LocalDate.now(ZoneId.systemDefault()).minus(Period.ofDays(90));
            // Checks whether this is an old employee.
            if (continuousServiceDate.isBefore(ninetyDaysAgo) || hasCompletedAnEthicsLiveTraining) {
                dueDate = LocalDate.of(LocalDate.now().getYear(), 12,31);
            }
            else {
                dueDate = DateUtils.addDays(continuousServiceDate,90);
            }
        }
        return dueDate;
    }
}
