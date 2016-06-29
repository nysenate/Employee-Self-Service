package gov.nysenate.ess.core.service.notification.base.header.notificationType;

import gov.nysenate.ess.core.service.notification.base.header.base.Header;

/**
 * The tyoe of notification.
 * Created by Chenguang He  on 6/15/2016.
 */
public interface NotificationType {
    StringBuilder notificationType = new StringBuilder(Header.header).append("." + NotificationType.class.getName());

}
