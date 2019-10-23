package gov.nysenate.ess.core.dao.pec.assignment;

public class PersonnelTaskAssignmentNotFoundEx extends RuntimeException {

    private final int empId;
    private final int taskId;

    public PersonnelTaskAssignmentNotFoundEx(int empId, int taskId) {
        super("Could not locate the specified personnel assigned task - " +
                "empId:" + empId + " taskId:" + taskId);
        this.empId = empId;
        this.taskId = taskId;
    }

    public int getEmpId() {
        return empId;
    }

    public int getTaskId() {
        return taskId;
    }
}
