package gov.nysenate.ess.core.model.pec;

import java.util.Objects;

public record TaskAssignmentDetails(PersonnelTaskAssignment assignment, PersonnelTask task) {
    public TaskAssignmentDetails {
        Objects.requireNonNull(assignment);
        Objects.requireNonNull(task);
    }

    public TaskAssignmentDetails(int empId, PersonnelTask task) {
        this(PersonnelTaskAssignment.newTask(empId, task.getTaskId()), task);
    }

    public boolean isCompleted() {
        return assignment.isCompleted();
    }

    public boolean isAssignmentActive() {
        return assignment.isActive();
    }

    public boolean isTaskActive() {
        return task.isActive();
    }

    /**
     * An assignment is actionable if the employee can and should complete it.
     * @return true if this assignment is actionable.
     */
    public boolean isActionable() {
        return !isCompleted() && isAssignmentActive() && isTaskActive();
    }

    /**
     * This assignment was never completed but can no longer be completed.
     * Either the assignment or the task has been inactivated.
     * @return
     */
    public boolean isObsolete() {
        return !isActionable() && !isCompleted();
    }

    public boolean canMarkComplete() {
        return !isCompleted() && isAssignmentActive();
    }

    public boolean canDeactivateAssignment() {
        return !isCompleted() && isAssignmentActive();
    }

    public boolean canReactivateAssignment() {
        return !isCompleted() && !isAssignmentActive() && isTaskActive();
    }
}
