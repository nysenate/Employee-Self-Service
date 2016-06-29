package gov.nysenate.ess.core.service.notification.base.header.eventType;

import gov.nysenate.ess.core.service.notification.base.header.base.Header;

/**
 * The priority class is used to mark the message in order to notice the event buss  which message send first.
 * Created by Chenguang He  on 6/15/2016.
 */
public interface Priority extends Header {
    StringBuilder eventType = new StringBuilder(EventType.eventType).append("." + Priority.class.getName());

    Priorities getPriority();

    void setPriority(Priorities priority);

    enum Priorities {
        High, Normal, Low
    }
}
