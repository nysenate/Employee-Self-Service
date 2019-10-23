package gov.nysenate.ess.core.client.view.pec;

import gov.nysenate.ess.core.client.view.base.ViewObject;

public class PersonnelTaskAssignmentIdView implements ViewObject {

    private final int empId;
    private final int taskId;

    public PersonnelTaskAssignmentIdView(int empId, int taskId) {
        this.empId = empId;
        this.taskId = taskId;
    }

    @Override
    public String getViewType() {
        return "personnel-assigned-task-id";
    }

    public int getEmpId() {
        return empId;
    }

    public int getTaskId() {
        return taskId;
    }
}
