package gov.nysenate.ess.core.service.pec.notification;

import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility methods for generating email HTML.
 */
@Service
public class PecEmailUtils {
    private static final String assignLengthStr =
            "You have %d days to complete this assignment from your hiring date." +
            " It is due by %s.<br>";
    private static final Map<PersonnelTaskType, Function<LocalDate, String>> taskDueDateMap = new HashMap<>();
    static {
        taskDueDateMap.put(PersonnelTaskType.MOODLE_COURSE,
                date -> assignLengthStr.formatted(30, date));
        taskDueDateMap.put(PersonnelTaskType.ETHICS_LIVE_COURSE, PecEmailUtils::getEthicsLiveDetails);
    }
    private static final String standardHtml = "<b>%s, our records indicate you have outstanding tasks to complete for Personnel.</b><br>" +
            "You can find instructions to complete them by logging into ESS, " +
            "then clicking the My Info tab and clicking on the To Do List. " +
            "Or, go to this link <a href=\"%s\">HERE</a><br><br>" +
            "<b>You must complete the following tasks: </b><br>",
    completionHtml = "Our records have been updated to indicate you have completed %s.";

    @Value("${domain.url}")
    private String domainUrl;
    @Value("${pec.test.mode:true}")
    private boolean pecTestMode;
    private final EmployeeDao employeeDao;
    private final EmployeeInfoService employeeInfoService;

    @Autowired
    public PecEmailUtils(EmployeeDao employeeDao, EmployeeInfoService employeeInfoService) {
        this.employeeDao = employeeDao;
        this.employeeInfoService = employeeInfoService;
    }

    public void addStandardHtml(EmployeeEmail emailInfo) {
        // HTML should already be set for these.
        if (emailInfo.type() == PecEmailType.ADMIN_CODES || emailInfo.type() == PecEmailType.REPORT_MISSING) {
            return;
        }
        if (emailInfo.type() == PecEmailType.COMPLETION) {
            emailInfo.withHtml(completionHtml.formatted(emailInfo.first().getTitle()));
            return;
        }
        String html = standardHtml.formatted(emailInfo.getEmployee().getFullName(), domainUrl + "/myinfo/personnel/todo") +
                PecEmailUtils.getTaskMapHtml(emailInfo.taskMap());
        if (pecTestMode) {
            html += "<br> Employee ID: #" + emailInfo.getEmployee().getEmployeeId() + "<br> Email: " + emailInfo.getEmployee().getEmail();
        }
        emailInfo.withHtml(html);
    }

    public List<EmployeeEmail> getEmails(List<String> addresses, PecEmailType type,
                                         String baseHtml, PersonnelTask... tasks) {
        var taskMap = new HashMap<PersonnelTask, LocalDate>();
        for (PersonnelTask task : tasks) {
            taskMap.put(task, null);
        }
        var emails = new ArrayList<EmployeeEmail>();
        for (String address : addresses) {
            Employee emp = employeeDao.getEmployeeByEmail(address);
            if (!emp.isSenator()) {
                String dear = type == PecEmailType.ADMIN_CODES ?
                        "Dear " + emp.getFullName() + ", " : "";
                emails.add(new EmployeeEmail(emp, type, taskMap).withHtml(dear + baseHtml));
            }
        }
        return emails;
    }

    public EmployeeEmail getEmail(int empId, PecEmailType type, PersonnelTask task, LocalDateTime dueDate) {
        var taskMap = new HashMap<PersonnelTask, LocalDate>();
        taskMap.put(task, dueDate == null ? null : dueDate.toLocalDate());
        Employee emp = employeeInfoService.getEmployee(empId);
        if (emp.isSenator()) {
            return null;
        }
        return new EmployeeEmail(emp, type, taskMap);
    }

    public List<Employee> getActiveNonSenatorEmployees() {
        return employeeDao.getActiveEmployees().stream()
                .filter(emp -> !emp.isSenator()).collect(Collectors.toList());
    }

    public static String getMissingEmailHtml(List<Employee> employeesWithMissingEmails) {
        return employeesWithMissingEmails.stream()
                .map(employee -> "NAME: %s EMPID: %s".formatted(employee.getFullName(), employee.getEmployeeId()))
                .collect(Collectors.joining("<br>"));
    }

    /**
     * Converts a map of data to properly formatted HTML.
     */
    private static String getTaskMapHtml(Map<PersonnelTask, LocalDate> taskDateMap) {
        var html = new StringBuilder("<ul>");
        for (var entry : taskDateMap.entrySet()) {
            html.append("<li>").append(entry.getKey().getTitle());
            String fragment = taskDueDateMap
                    .getOrDefault(entry.getKey().getTaskType(), date -> "")
                    .apply(entry.getValue());
            if (!fragment.isEmpty()) {
                html.append(": ").append(fragment);
            }
            html.append("</li>");
        }
        return html + "</ul>";
    }

    private static String getEthicsLiveDetails(LocalDate dueDate) {
        String ethicsLiveStr;
        if (dueDate.getMonth() == Month.DECEMBER && dueDate.getDayOfMonth() == 31) {
            ethicsLiveStr = "You have until the end of the current calendar year to complete this course.";
        }
        else {
            ethicsLiveStr = assignLengthStr.formatted(90, dueDate);
        }
        return ethicsLiveStr + " These live sessions run once a month.<br>";
    }
}
