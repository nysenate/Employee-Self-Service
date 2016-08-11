package gov.nysenate.ess.seta.service.notification;

import gov.nysenate.ess.seta.model.attendance.TimeRecord;

import java.util.Collection;

/**
 * A service responsible for sending email reminders from a supervisor to an employee
 * indicating that the employee needs to submit a time record
 */
public interface TimeRecordEmailService {

    /**
     * Sends an email to each employee with a time record on the given list.
     * The email will contain a reminder to submit each time record on the list that belongs to the employee
     * @param supId
     * @param timeRecords {@link Collection<TimeRecord>}
     */
    void sendEmailReminders(Integer supId, Collection<TimeRecord> timeRecords);
}
