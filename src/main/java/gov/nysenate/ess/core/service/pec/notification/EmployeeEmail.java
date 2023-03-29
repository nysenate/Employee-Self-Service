package gov.nysenate.ess.core.service.pec.notification;

import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.personnel.Employee;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains data about a PEC email to be sent to an employee.
 */
public class EmployeeEmail {
    private final PecEmailType type;
    private String html = "";
    private final Employee employee;
    private final Map<PersonnelTask, LocalDate> taskMap = new HashMap<>();

    public EmployeeEmail(Employee to, PecEmailType type,
                         Map<PersonnelTask, LocalDate> taskMap) {
        this.employee = to;
        this.type = type;
        this.taskMap.putAll(taskMap);
    }

    public String subject() {
        return type.getSubject(first());
    }

    public PersonnelTask first() {
        return taskMap.isEmpty() ? null : taskMap.keySet().iterator().next();
    }

    public String html() {
        return html;
    }

    public PecEmailType type() {
        return type;
    }

    public Employee getEmployee() {
        return employee;
    }

    public Map<PersonnelTask, LocalDate> taskMap() {
        return taskMap;
    }

    public EmployeeEmail withHtml(String html) {
        this.html = html;
        return this;
    }
}
