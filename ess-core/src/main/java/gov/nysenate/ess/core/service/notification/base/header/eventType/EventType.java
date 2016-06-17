package gov.nysenate.ess.core.service.notification.base.header.eventType;

import gov.nysenate.ess.core.service.notification.base.header.base.Header;

/**
 * Created by senateuser on 6/15/2016.
 */
public interface EventType {
    StringBuilder eventType = new StringBuilder(Header.header).append("." + EventType.class.getName());

}
