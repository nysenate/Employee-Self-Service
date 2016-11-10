package gov.nysenate.ess.time.service.notification;

import gov.nysenate.ess.time.model.attendance.TimeRecord;

/**
 * Contains methods that can send a rejection notice email for a rejected time record
 */
public interface DisapprovalEmailService {

    /**
     * Send an email to the employee of the given record notifying them of its rejection
     * @param rejectedRecord {@link TimeRecord} - rejected time record
     * @param rejectorId
     */
    void sendRejectionMessage(TimeRecord rejectedRecord, int rejectorId);
}
