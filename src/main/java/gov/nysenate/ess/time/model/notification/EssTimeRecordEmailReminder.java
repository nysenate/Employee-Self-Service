package gov.nysenate.ess.time.model.notification;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.time.model.attendance.TimeRecord;

import java.util.List;

/**
 * Represents a time record email reminder to be sent to the specified
 * employee for the specified time records.
 *
 * wasReminderSent tracks whether the email was delivered successfully or not.
 */
public class EssTimeRecordEmailReminder {

    private final Employee employee;
    private final List<TimeRecord> timeRecords;
    private boolean wasReminderSent;

    public EssTimeRecordEmailReminder(Employee employee, List<TimeRecord> timeRecords) {
        this.employee = employee;
        this.timeRecords = timeRecords;
        this.wasReminderSent = false;
    }

    public Employee getEmployee() {
        return employee;
    }

    public List<TimeRecord> getTimeRecords() {
        return timeRecords;
    }

    public boolean wasReminderSent() {
        return wasReminderSent;
    }

    public void setWasReminderSent(boolean wasReminderSent) {
        this.wasReminderSent = wasReminderSent;
    }
}
