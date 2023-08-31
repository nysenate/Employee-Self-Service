package gov.nysenate.ess.core.service.pec.notification;

import gov.nysenate.ess.core.model.pec.PersonnelTask;

/**
 * Contains some common information about types of emails.
 */
public enum PecEmailType {
    INVITE, REMINDER, COMPLETION,
    REPORT_MISSING, ADMIN_CODES;

    public String getSubject(PersonnelTask task) {
        return switch (this) {
            case INVITE -> "You have to complete the task: " + task.getTitle();
            case REMINDER -> "You have outstanding Personnel Tasks.";
            case COMPLETION -> "You have completed the task: " + task.getTitle();
            case REPORT_MISSING -> "Employees With Missing Emails";
            case ADMIN_CODES -> "New Codes for Ethics Live Course: " + task.getTitle();
        };
    }
}