package gov.nysenate.ess.core.service.pec.notification;

import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;

// TODO: Candidate for record in Java 17
public class AssignmentWithTask {
    private final PersonnelTaskAssignment assignment;
    private final PersonnelTask task;

    public AssignmentWithTask(int empId, PersonnelTask task) {
        this(PersonnelTaskAssignment.newTask(empId, task.getTaskId()), task);
    }

    public AssignmentWithTask(PersonnelTaskAssignment assignment, PersonnelTask task) {
        this.assignment = assignment;
        this.task = task;
    }

    public PersonnelTaskAssignment assignment() {
        return assignment;
    }

    public PersonnelTask task() {
        return task;
    }

}
