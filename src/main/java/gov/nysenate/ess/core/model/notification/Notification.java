package gov.nysenate.ess.core.model.notification;

public class Notification
{
    protected NotificationLevel level;
    protected String message;
    protected String origin;

    public Notification() {}

    public NotificationLevel getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public String getOrigin() {
        return origin;
    }
}