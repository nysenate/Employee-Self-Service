package gov.nysenate.ess.core.client.view.pec;

import gov.nysenate.ess.core.model.pec.PersonnelAssignedTask;

/**
 * Personnel assigned task with task details attached.
 */
public class DetailPersonnelAssignedTaskView extends PersonnelAssignedTaskView {

    private final PersonnelTaskView taskDetails;

    public DetailPersonnelAssignedTaskView(PersonnelAssignedTask task, PersonnelTaskView taskDetails) {
        super(task);
        this.taskDetails = taskDetails;
    }

    public PersonnelTaskView getTaskDetails() {
        return taskDetails;
    }

    @Override
    public String getViewType() {
        return "detailed-" + super.getViewType();
    }
}
