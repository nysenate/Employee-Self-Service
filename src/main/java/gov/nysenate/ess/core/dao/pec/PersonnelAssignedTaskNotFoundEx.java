package gov.nysenate.ess.core.dao.pec;

import gov.nysenate.ess.core.model.pec.PersonnelTaskId;

public class PersonnelAssignedTaskNotFoundEx extends RuntimeException {

    private final int empId;
    private final PersonnelTaskId taskId;

    public PersonnelAssignedTaskNotFoundEx(int empId, PersonnelTaskId taskId) {
        super("Could not locate the specified personnel assigned task - " +
                "empId:" + empId + " taskId:" + taskId);
        this.empId = empId;
        this.taskId = taskId;
    }

    public int getEmpId() {
        return empId;
    }

    public PersonnelTaskId getTaskId() {
        return taskId;
    }
}
