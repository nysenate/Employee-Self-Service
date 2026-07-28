package gov.nysenate.ess.core.model.pec;

import gov.nysenate.ess.core.client.view.DetailedEmployeeView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.Comparator;
import java.util.List;

public class EmployeeTaskAssignmentsView implements ViewObject {
    private DetailedEmployeeView employee;
    private List<TaskAssignmentDetailsView> allAssignments;
    private List<TaskAssignmentDetailsView> incompleteAssignments;
    private List<TaskAssignmentDetailsView> obsoleteAssignments;
    private List<TaskAssignmentDetailsView> completedAssignments;
    private int incompleteCount;
    private int activeCount;
    private EmployeeTaskAssignments.CompletionStatus completionStatus;

    public EmployeeTaskAssignmentsView(EmployeeTaskAssignments empAssignments) {
        this.employee = new DetailedEmployeeView(empAssignments.employee());
        this.allAssignments = toSortedTaskAssignmentViews(empAssignments.assignments());
        this.incompleteAssignments = toSortedTaskAssignmentViews(empAssignments.incompleteAssignments());
        this.obsoleteAssignments = toSortedTaskAssignmentViews(empAssignments.obsoleteAssignments());
        this.completedAssignments = toSortedTaskAssignmentViews(empAssignments.completedAssignments());
        this.incompleteCount = empAssignments.incompleteCount();
        this.activeCount = empAssignments.activeCount();
        this.completionStatus = empAssignments.completionStatus();
    }

    private static List<TaskAssignmentDetailsView> toSortedTaskAssignmentViews(List<TaskAssignmentDetails> assignments) {
        return assignments.stream()
                .map(TaskAssignmentDetailsView::new)
                .sorted(Comparator.comparingInt(TaskAssignmentDetailsView::getTaskId).reversed())
                .toList();
    }

    public DetailedEmployeeView getEmployee() {
        return employee;
    }

    public List<TaskAssignmentDetailsView> getAllAssignments() {
        return allAssignments;
    }

    public List<TaskAssignmentDetailsView> getIncompleteAssignments() {
        return incompleteAssignments;
    }

    public List<TaskAssignmentDetailsView> getObsoleteAssignments() {
        return obsoleteAssignments;
    }

    public List<TaskAssignmentDetailsView> getCompletedAssignments() {
        return completedAssignments;
    }

    public int getIncompleteCount() {
        return incompleteCount;
    }

    public int getActiveCount() {
        return activeCount;
    }

    public EmployeeTaskAssignments.CompletionStatus getCompletionStatus() {
        return completionStatus;
    }

    @Override
    public String getViewType() {
        return "employee-task-assignments";
    }
}
