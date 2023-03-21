package gov.nysenate.ess.core.service.pec.notification;

import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.personnel.Employee;

import java.util.List;

/**
 * Contains data about a PEC email to be sent to an employee.
 */
public class EmployeeEmail extends NotificationEmail {
    private final Employee employee;

    public EmployeeEmail(Employee to, EmailType type, PersonnelTask task) {
        this(to, type, List.of(task));
    }

    public EmployeeEmail(Employee to, EmailType type, List<PersonnelTask> tasks) {
        super(to.getEmail(), type, tasks);
        this.employee = to;
    }

    public Employee getEmployee() {
        return employee;
    }
}
