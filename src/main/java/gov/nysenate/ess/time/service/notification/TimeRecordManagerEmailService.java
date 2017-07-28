package gov.nysenate.ess.time.service.notification;

import gov.nysenate.ess.time.service.attendance.TimeRecordManager;
import gov.nysenate.ess.time.service.attendance.TimeRecordManagerError;

import java.util.Collection;

/**
 * A service that sends email notifications based on events that occur
 * in the {@link TimeRecordManager}
 */
public interface TimeRecordManagerEmailService {

    /**
     * Sends an email, notifying the administrator of an error that occurred
     * within the {@link TimeRecordManager}
     *  @param exceptions Throwable
     *
     */
    void sendTrmErrorNotification(Collection<TimeRecordManagerError> exceptions);
}
