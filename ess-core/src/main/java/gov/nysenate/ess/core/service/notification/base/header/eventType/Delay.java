package gov.nysenate.ess.core.service.notification.base.header.eventType;

import gov.nysenate.ess.core.service.notification.base.header.base.Header;

/**
 * Created by senateuser on 6/14/2016.
 */
public interface Delay extends Header {
    StringBuilder eventType = new StringBuilder(EventType.eventType).append("." + Delay.class.getName());

    long getDelayTime();

    void setDelayTime(Long delayTime);
}
