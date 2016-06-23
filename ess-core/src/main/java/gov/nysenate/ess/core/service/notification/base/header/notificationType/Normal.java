package gov.nysenate.ess.core.service.notification.base.header.notificationType;

/**
 * Normal notification is the default notification type
 * Created by Chenguang He  on 6/15/2016.
 */
public interface Normal extends NotificationType {
    StringBuilder notificationType = new StringBuilder(NotificationType.notificationType).append("." + Normal.class.getName());
}
