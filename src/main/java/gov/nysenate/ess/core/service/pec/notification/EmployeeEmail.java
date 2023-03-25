package gov.nysenate.ess.core.service.pec.notification;

import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.personnel.Employee;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Contains data about a PEC email to be sent to an employee.
 */
public class EmployeeEmail extends NotificationEmail {
    private static final String standardHtml = "<b>%s, our records indicate you have outstanding tasks to complete for Personnel.</b><br>" +
            "You can find instructions to complete them by logging into ESS, " +
            "then clicking the My Info tab and clicking on the To Do List. " +
            "Or, go to this link <a href=\"%s\">HERE</a><br><br>" +
            "<b>You must complete the following tasks: </b><br><br>\n\n";

    @Value("${domain.url}")
    private String domainUrl;
    private final Employee employee;
    private final Map<PersonnelTask, LocalDate> taskMap;

    public EmployeeEmail(Employee to, EmailType type, PersonnelTask task) {
        // Must be a completion email, so due date no longer matters.
        this(to, type, Map.of(task, LocalDate.now()));
        this.html = "Our records have been updated to indicate you have completed " + task.getTitle() + ".";
    }

    public EmployeeEmail(Employee to, EmailType type,
                         Map<PersonnelTask, LocalDate> taskMap,
                         boolean pecTestMode) {
        this(to, type, taskMap);
        this.html = standardHtml.formatted(employee.getFullName(), domainUrl + "/myinfo/personnel/todo") +
                PecEmailUtils.getTaskMapHtml(taskMap);
        if (pecTestMode) {
            this.html += "<br> Employee ID: #" + employee.getEmployeeId() + "<br> Email: " + employee.getEmail();
        }
    }

    private EmployeeEmail(Employee to, EmailType type, Map<PersonnelTask, LocalDate> taskMap) {
        super(to.getEmail(), type);
        this.employee = to;
        this.taskMap = taskMap;
    }

    public Employee getEmployee() {
        return employee;
    }

    @Override
    public boolean isLimited() {
        return true;
    }

    @Override
    public List<PersonnelTask> tasks() {
        return new ArrayList<>(taskMap.keySet());
    }
}
