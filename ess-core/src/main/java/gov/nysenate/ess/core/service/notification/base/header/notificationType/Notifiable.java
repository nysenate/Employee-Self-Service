package gov.nysenate.ess.core.service.notification.base.header.notificationType;

/**
 * notifiable is used to let user notice the message as much as possible.
 * Created by Chenguang He  on 6/15/2016.
 */
public interface Notifiable extends NotificationType {
    StringBuilder notificationType = new StringBuilder(NotificationType.notificationType).append("." + Notifiable.class.getName());
}
