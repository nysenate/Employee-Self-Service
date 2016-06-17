package gov.nysenate.ess.core.service.notification.base.header.notificationType;

import gov.nysenate.ess.core.service.notification.base.header.base.Header;

/**
 * Created by senateuser on 6/15/2016.
 */
public interface NotificationType {
    StringBuilder notificationType = new StringBuilder(Header.header).append("." + NotificationType.class.getName());

}
