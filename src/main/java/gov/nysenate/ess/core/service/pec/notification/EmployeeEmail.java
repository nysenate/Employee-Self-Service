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
            "<b>%s, our records indicate you have outstanding tasks to complete for Personnel.</b><br>" +
                    "You can find instructions to complete them by logging into ESS, " +
                    "then clicking the My Info tab and clicking on the To Do List. " +
                    "Or, go to this link <a href=\"%s\">HERE</a><br><br>" +
                    "<b>You must complete the following tasks: </b><br>",
            completionHtml = "Our records have been updated to indicate you have completed %s.",
            assignLengthStr = "You have %d days to complete this assignment from your hiring date. It is due by %s.<br>";

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
                    html.append("<b>").append("This is past its due date.").append("</b>");
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
        if (type == PersonnelTaskType.MOODLE_COURSE) {
            return Optional.of(assignLengthStr.formatted(30, dueDate));
        }
        if (type != PersonnelTaskType.ETHICS_LIVE_COURSE) {
            return Optional.empty();
        }
        String ethicsLiveStr;
        if (dueDate.getMonth() == Month.DECEMBER && dueDate.getDayOfMonth() == 31) {
            ethicsLiveStr = "You have until the end of the current calendar year to complete this course.";
        }
        else {
            ethicsLiveStr = assignLengthStr.formatted(90, dueDate);
        }
        return Optional.of(ethicsLiveStr + " These live sessions run once a month.<br>");
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
