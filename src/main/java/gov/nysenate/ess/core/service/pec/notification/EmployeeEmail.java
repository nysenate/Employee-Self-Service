package gov.nysenate.ess.core.service.pec.notification;

import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.personnel.Employee;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

/**
 * Contains data about a PEC email to be sent to an employee.
 */
public class EmployeeEmail {
    private static final String standardHtml =
            "<b>%s, our records indicate you have outstanding trainings to complete which is mandatory by law.</b><br>" +
                    "You can find instructions to complete them by logging into ESS, " +
                    "then clicking the My Info tab and clicking on the To Do List. " +
                    "Or, go to this link <a href=\"%s\">HERE</a><br><br>" +
                    "<b>You must complete the following trainings: </b><br>",
            completionHtml = "Our records have been updated to indicate you have completed %s.",
            assignLengthStr = "You have %d days from your hiring date to complete this assignment. It is due by %s.<br>";

    private final PecEmailType type;
    private final Employee employee;
    private final List<AssignmentWithTask> dataList;
    private final String html;

    public EmployeeEmail(Employee to, PecEmailType type,
                         List<AssignmentWithTask> dataList, List<String> extraData) {
        this.employee = to;
        this.type = type;
        this.dataList = dataList;
        this.html = getHtml(extraData);
    }

    /**
     * Converts a map of data to properly formatted HTML.
     */
    private static String getTaskMapHtml(List<AssignmentWithTask> dataList) {
        var html = new StringBuilder("<ul>");
        for (var data : dataList) {
            var task = data.task();
            LocalDateTime dueDateTime = data.assignment().getDueDate();
            html.append("<li>").append(data.task().getTitle());
            var strOpt = getDetails(task.getTaskType(), dueDateTime);
            if (strOpt.isPresent()) {
                html.append(": ").append(strOpt.get());
                if (LocalDate.now().isAfter(dueDateTime.toLocalDate())) {
                    html.append("<b>").append("This is past its due date. Failure to complete this mandatory training may result in the holding of your paycheck.").append("</b>");
                }
            }
            html.append("</li>");
        }
        return html + "</ul>";
    }

    private static Optional<String> getDetails(PersonnelTaskType type, LocalDateTime dueDateTime) {
        if (dueDateTime == null) {
            return Optional.empty();
        }
        LocalDate dueDate = dueDateTime.toLocalDate();
        String unambiguousDate = dueDate.getMonth() + ", " + dueDate.getDayOfMonth() + " " + dueDate.getYear();
        if (type == PersonnelTaskType.MOODLE_COURSE) {
            return Optional.of(assignLengthStr.formatted(30, unambiguousDate));
        }
        String ethicsLiveStr;
        if (type == PersonnelTaskType.ETHICS_LIVE_COURSE) {
            ethicsLiveStr = assignLengthStr.formatted(90, unambiguousDate);
            return Optional.of(ethicsLiveStr + " These live sessions are run a limited amount of times a month.<br>");
        }
        return Optional.empty();
    }

    private String getHtml(List<String> extraData) {
        return switch (type) {
            case REPORT_MISSING -> String.join("<br>", extraData);
            case ADMIN_CODES -> "Dear " + employee.getFullName() + ", the new codes are <br>" +
                    "CODE 1: " + extraData.get(0) + "<br>" + "CODE 2: " + extraData.get(1);
            case INVITE, REMINDER -> standardHtml.formatted(employee.getFullName(),
                    extraData.get(0) + "/myinfo/personnel/todo") + getTaskMapHtml(dataList);
            case COMPLETION -> completionHtml.formatted(first().getTitle());
        };
    }

    public String subject() {
        return type.getSubject(first());
    }

    public PersonnelTask first() {
        return dataList.isEmpty() ? null : dataList.get(0).task();
    }

    public PecEmailType type() {
        return type;
    }

    public Employee employee() {
        return employee;
    }

    public List<AssignmentWithTask> dataList() {
        return dataList;
    }

    public String html() {
        return html;
    }
}
