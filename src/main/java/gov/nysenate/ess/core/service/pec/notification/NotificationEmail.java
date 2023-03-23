package gov.nysenate.ess.core.service.pec.notification;

import gov.nysenate.ess.core.model.pec.PersonnelTask;

import java.util.List;

/**
 * Contains some data about a PEC email.
 */
public abstract class NotificationEmail {
    private final String to;
    private final EmailType type;
    protected String html = "";

    public NotificationEmail(String to, EmailType type) {
        this.to = to;
        this.type = type;
    }

    public String sendTo() {
        return to;
    }

    public String subject() {
        return type.getSubject(tasks().isEmpty() ? null : tasks().get(0));
    }

    public String html() {
        return html;
    }

    public abstract List<PersonnelTask> tasks();
}
