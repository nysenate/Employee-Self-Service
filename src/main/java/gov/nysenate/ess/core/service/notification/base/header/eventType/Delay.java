package gov.nysenate.ess.core.service.notification.base.header.eventType;

import gov.nysenate.ess.core.service.notification.base.header.base.Header;

/**
 *  delay header is used to represent the message will be sent after a time interval.
 * Created by Chenguang He  on 6/15/2016.
 */
public interface Delay extends Header {
    StringBuilder eventType = new StringBuilder(EventType.eventType).append("." + Delay.class.getName());

    /**
     * how many time in millisecond wait before sending  message
     *
     * @return time interval
     */
    long getDelayTime();

    /*
    set the time interval
     */
    void setDelayTime(Long delayTime);
}
