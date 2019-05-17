package gov.nysenate.ess.core.client.view.pec;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.pec.PersonnelTaskId;

public class PersonnelAssignedTaskIdView implements ViewObject {

    private final int empId;
    private final PersonnelTaskIdView taskId;

    public PersonnelAssignedTaskIdView(int empId, PersonnelTaskId taskId) {
        this.empId = empId;
        this.taskId = new PersonnelTaskIdView(taskId);
    }

    @Override
    public String getViewType() {
        return "personnel-assigned-task-id";
    }

    public int getEmpId() {
        return empId;
    }

    public PersonnelTaskIdView getTaskId() {
        return taskId;
    }
}
