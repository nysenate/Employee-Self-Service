package gov.nysenate.ess.time.service.notification;

import com.google.common.collect.Multimap;
import gov.nysenate.ess.time.model.attendance.TimeRecord;

import java.time.LocalDate;
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
     * @param recordDates
     */
    void sendEmailReminders(Integer supId, Multimap<Integer, LocalDate> recordDates);
}
