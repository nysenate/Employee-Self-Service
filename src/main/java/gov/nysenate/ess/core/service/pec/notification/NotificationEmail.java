package gov.nysenate.ess.core.service.pec.notification;

import gov.nysenate.ess.core.model.pec.PersonnelTask;

import java.util.List;

/**
 * Contains some data about a PEC email.
 */
public class NotificationEmail {
    private final String to;
    private final EmailType type;
    private final List<PersonnelTask> tasks;

    public NotificationEmail(String to, EmailType type) {
        this(to, type, List.of());
    }

    public NotificationEmail(String to, EmailType type, PersonnelTask task) {
        this(to, type, List.of(task));
    }

    public NotificationEmail(String to, EmailType type, List<PersonnelTask> tasks) {
        this.to = to;
        this.type = type;
        this.tasks = tasks;
    }

    public String sendTo() {
        return to;
    }

    public String subject() {
        return type.getSubject(tasks.isEmpty() ? null : tasks.get(0));
    }

    public List<PersonnelTask> tasks() {
        return tasks;
    }
}
