package gov.nysenate.ess.core.service.pec.notification;

import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;

public record AssignmentWithTask(PersonnelTaskAssignment assignment, PersonnelTask task) {
    public AssignmentWithTask(int empId, PersonnelTask task) {
        this(PersonnelTaskAssignment.newTask(empId, task.getTaskId()), task);
    }
}
