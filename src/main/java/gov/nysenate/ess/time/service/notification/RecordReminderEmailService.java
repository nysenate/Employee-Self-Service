package gov.nysenate.ess.time.service.notification;

import com.google.common.collect.Multimap;

import java.time.LocalDate;

/**
 * A service responsible for sending email reminders from a supervisor to an employee
 * indicating that the employee needs to submit a time record
 */
public interface RecordReminderEmailService {

    /**
     * Sends an email to each employee with a time record on the given list.
     * The email will contain a reminder to submit each time record on the list that belongs to the employee.
     *
     * @param supId Integer
     * @param recordDates Multimap<Integer, LocalDate>
     *
     * @throws InactiveEmployeeEmailEx if any of the given empIds are for inactive employees
     */
    void sendEmailReminders(Integer supId, Multimap<Integer, LocalDate> recordDates) throws InactiveEmployeeEmailEx;
}
