package gov.nysenate.ess.core.service.notification.base.header.notificationType;

/**
 * Created by senateuser on 6/14/2016.
 */
public interface Notifiable extends NotificationType {
    StringBuilder notificationType = new StringBuilder(NotificationType.notificationType).append("." + Notifiable.class.getName());
}
