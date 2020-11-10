package gov.nysenate.ess.time.service.notification;

import com.google.common.collect.Multimap;
import gov.nysenate.ess.time.model.notification.EssTimeRecordEmailReminder;

import java.time.LocalDate;
import java.util.List;

/**
 * A service responsible for sending email reminders from a supervisor to an employee
 * indicating that the employee needs to submit a time record
 */
public interface RecordReminderEmailService {

    /**
     * Sends a email for each emailReminder.
     * The email will contain a reminder to submit each time record in the {@code emailReminder}.
     *
     * @param emailReminders A collection of email reminders to be sent.
     * @return A List of reminders with the 'wasReminderSent' field updated.
     */
    List<EssTimeRecordEmailReminder> sendEmailReminders(List<EssTimeRecordEmailReminder> emailReminders);
}
