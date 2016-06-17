package gov.nysenate.ess.core.service.notification.base.header.eventType;

import gov.nysenate.ess.core.service.notification.base.header.base.Header;

/**
 * Created by senateuser on 6/14/2016.
 */
public interface Simple extends Header {
    StringBuilder eventType = new StringBuilder(EventType.eventType).append("." + Simple.class.getName());
}
