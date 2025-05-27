package gov.nysenate.ess.core.service.pec.task;

public class PersonnelTaskNotFoundEx extends RuntimeException {

    private final int taskId;

    public PersonnelTaskNotFoundEx(int taskId) {
        super("Could not find a personnel task with the given id: " + taskId);
        this.taskId = taskId;
    }

    public int getTaskId() {
        return taskId;
    }
}
