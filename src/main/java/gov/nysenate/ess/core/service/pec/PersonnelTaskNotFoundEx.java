package gov.nysenate.ess.core.service.pec;

import gov.nysenate.ess.core.model.pec.PersonnelTaskId;

public class PersonnelTaskNotFoundEx extends RuntimeException {

    private final PersonnelTaskId taskId;

    public PersonnelTaskNotFoundEx(PersonnelTaskId taskId) {
        super("Could not find a personnel task with the given id: " + taskId);
        this.taskId = taskId;
    }

    public PersonnelTaskId getTaskId() {
        return taskId;
    }
}
