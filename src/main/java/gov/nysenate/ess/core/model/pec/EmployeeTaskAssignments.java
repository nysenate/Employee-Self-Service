package gov.nysenate.ess.core.model.pec;

import gov.nysenate.ess.core.model.personnel.Employee;

import java.util.List;
import java.util.Objects;

/**
 * Personnel task assignments for one employee, with derived groups used by To-Do Reporting.
 * <p>
 * - Active/current assignments are incomplete assignments where both the assignment and its
 * training task are active.
 * - Obsolete assignments are incomplete assignments that are no longer actionable because
 * either the assignment or task is inactive.
 * - Completed assignments are grouped separately regardless of active status.
 */
public record EmployeeTaskAssignments(Employee employee, List<TaskAssignmentDetails> assignments) {
    public enum CompletionStatus {
        ALL_OUTSTANDING,
        PARTIAL,
        ALL_COMPLETE
    }

    public EmployeeTaskAssignments {
        Objects.requireNonNull(employee);
        assignments = List.copyOf(assignments);
    }

    /**
     * Incomplete assignments that still appear on the employee's To-Do List.
     */
    public List<TaskAssignmentDetails> incompleteAssignments() {
        return assignments.stream()
                .filter(TaskAssignmentDetails::isActionable)
                .toList();
    }

    /**
     * Incomplete assignments that no longer appear on the employee's To-Do List because the
     * assignment or training task is inactive.
     */
    public List<TaskAssignmentDetails> obsoleteAssignments() {
        return assignments.stream()
                .filter(TaskAssignmentDetails::isObsolete)
                .toList();
    }

    /**
     * Completed assignments, regardless of active status.
     */
    public List<TaskAssignmentDetails> completedAssignments() {
        return assignments.stream()
                .filter(TaskAssignmentDetails::isCompleted)
                .toList();
    }

    /**
     * Count of incomplete, actionable assignments.
     */
    public int incompleteCount() {
        return incompleteAssignments().size();
    }

    /**
     * Count of assignments where both the assignment and training task are active, regardless of
     * completion status.
     */
    public int activeCount() {
        return (int) assignments.stream()
                .filter(assignment -> assignment.isAssignmentActive() && assignment.isTaskActive())
                .count();
    }

    /**
     * Completion status across active/current assignments.
     */
    public CompletionStatus completionStatus() {
        if (incompleteCount() == 0) {
            return CompletionStatus.ALL_COMPLETE;
        }
        if (incompleteCount() == activeCount()) {
            return CompletionStatus.ALL_OUTSTANDING;
        }
        return CompletionStatus.PARTIAL;
    }
}
