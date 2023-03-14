package gov.nysenate.ess.core.service.pec.notification;

public class NotificationEmail {
    private final String to;
    private final String subject;
    private final String html;

    public NotificationEmail(String to, String subject, String html) {
        this.to = to.trim();
        this.subject = subject;
        this.html = html;
    }

    public String to() {
        return to;
    }

    public String subject() {
        return subject;
    }

    public String html() {
        return html;
    }
}
