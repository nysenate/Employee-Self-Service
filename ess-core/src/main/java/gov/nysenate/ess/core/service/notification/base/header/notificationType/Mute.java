package gov.nysenate.ess.core.service.notification.base.header.notificationType;

/**
 * An event is notifiable, if and only if the event can handle by its handler
 */
public interface Mute extends NotificationType {
    StringBuilder notificationType = new StringBuilder(NotificationType.notificationType).append("." + Mute.class.getName());

}
