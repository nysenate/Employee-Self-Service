package gov.nysenate.ess.core.service.notification.base.header.eventType;

import gov.nysenate.ess.core.service.notification.base.header.base.Header;

/**
 *  A simple message has no specific header.
 * Created by Chenguang He  on 6/15/2016.
 */
public interface Simple extends Header {
    StringBuilder eventType = new StringBuilder(EventType.eventType).append("." + Simple.class.getName());
}
