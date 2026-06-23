package gov.nysenate.ess.core.model.pec;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.pec.PersonnelTaskAssignmentView;
import gov.nysenate.ess.core.client.view.pec.PersonnelTaskView;

public class TaskAssignmentDetailsView extends PersonnelTaskAssignmentView {

    private PersonnelTaskView task;

    private boolean isCompleted;
    private boolean isAssignmentActive;
    private boolean isTaskActive;
    private boolean isActionable;
    private boolean isObsolete;
    private boolean canMarkComplete;
    private boolean canDeactivateAssignment;
    private boolean canReactivateAssignment;

    public TaskAssignmentDetailsView(TaskAssignmentDetails taskAssignmentDetails) {
        super(taskAssignmentDetails.assignment());
        this.task = new PersonnelTaskView(taskAssignmentDetails.task());
        this.isCompleted = taskAssignmentDetails.isCompleted();
        this.isAssignmentActive = taskAssignmentDetails.isAssignmentActive();
        this.isTaskActive = taskAssignmentDetails.isTaskActive();
        this.isActionable = taskAssignmentDetails.isActionable();
        this.isObsolete = taskAssignmentDetails.isObsolete();
        this.canMarkComplete = taskAssignmentDetails.canMarkComplete();
        this.canDeactivateAssignment = taskAssignmentDetails.canDeactivateAssignment();
        this.canReactivateAssignment = taskAssignmentDetails.canReactivateAssignment();
    }

    public PersonnelTaskView getTask() {
        return task;
    }

    @JsonProperty("isCompleted")
    public boolean isCompleted() {
        return isCompleted;
    }

    @JsonProperty("isAssignmentActive")
    public boolean isAssignmentActive() {
        return isAssignmentActive;
    }

    @JsonProperty("isTaskActive")
    public boolean isTaskActive() {
        return isTaskActive;
    }

    @JsonProperty("isActionable")
    public boolean isActionable() {
        return isActionable;
    }

    @JsonProperty("isObsolete")
    public boolean isObsolete() {
        return isObsolete;
    }

    @JsonProperty("canMarkComplete")
    public boolean isCanMarkComplete() {
        return canMarkComplete;
    }

    @JsonProperty("canDeactivateAssignment")
    public boolean isCanDeactivateAssignment() {
        return canDeactivateAssignment;
    }

    @JsonProperty("canReactivateAssignment")
    public boolean isCanReactivateAssignment() {
        return canReactivateAssignment;
    }

    @Override
    public String getViewType() {
        return "task-assignment-details";
    }
}
