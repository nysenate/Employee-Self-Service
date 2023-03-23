package gov.nysenate.ess.core.service.pec.notification;

import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.personnel.Employee;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility methods for generating email HTML.
 */
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

    private PecEmailUtils() {}

    /**
     * Converts a map of data to properly formatted HTML.
     */
    public static String getTaskMapHtml(Map<PersonnelTask, LocalDate> taskDateMap) {
        var html = new StringBuilder("<ul>");
        for (var entry : taskDateMap.entrySet()) {
            html.append("<li>");
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

    public static String getMissingEmailHtml(List<Employee> employeesWithMissingEmails) {
        return employeesWithMissingEmails.stream()
                .map(employee -> "NAME: %s EMPID: %s".formatted(employee.getFullName(), employee.getEmployeeId()))
                .collect(Collectors.joining("<br>"));
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
