package gov.nysenate.ess.core.service.pec.notification;

import gov.nysenate.ess.core.model.pec.PersonnelTask;

import java.util.function.Function;

/**
 * Contains some common information about types of emails.
 */
public enum EmailType {
    INVITE(task -> "You have to complete the task: " + task.getTitle()),
    REMINDER(task -> "You have outstanding Personnel Tasks."),
    COMPLETION(task -> "You have completed the task: " + task.getTitle()),
    REPORT_MISSING(task -> "Employees With Missing Emails"),
    ADMIN_CODES(task -> "New Codes for Ethics Live Course: " + task.getTitle());

    private final Function<PersonnelTask, String> getSubject;

    EmailType(Function<PersonnelTask, String> getSubject) {
        this.getSubject = getSubject;
    }

    public String getSubject(PersonnelTask task) {
        return getSubject.apply(task);
    }
}
