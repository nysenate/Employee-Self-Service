package gov.nysenate.ess.core.service.pec.notification;

import gov.nysenate.ess.core.model.pec.PersonnelTask;

import java.util.List;

/**
 * Class for email data where email is some report not sent to generic employees.
 */
public class ReportEmail extends NotificationEmail {
    private final List<PersonnelTask> tasks;

    public ReportEmail(String to, EmailType type, String html) {
        super(to, type);
        this.html = html;
        this.tasks = List.of();
    }

    public ReportEmail(String to, EmailType type, PersonnelTask task, String html) {
        super(to, type);
        this.html = html;
        this.tasks = List.of(task);
    }

    @Override
    public List<PersonnelTask> tasks() {
        return tasks;
    }
}
