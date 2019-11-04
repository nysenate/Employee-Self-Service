package gov.nysenate.ess.core.client.view.pec;

import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;

/**
 * Personnel assigned task with task details attached.
 */
public class DetailPersonnelTaskAssignmentView extends PersonnelTaskAssignmentView {

    private final PersonnelTaskView task;

    public DetailPersonnelTaskAssignmentView(PersonnelTaskAssignment task, PersonnelTaskView taskDetails) {
        super(task);
        this.task = taskDetails;
    }

    public PersonnelTaskView getTask() {
        return task;
    }

    @Override
    public String getViewType() {
        return "detailed-" + super.getViewType();
    }
}
