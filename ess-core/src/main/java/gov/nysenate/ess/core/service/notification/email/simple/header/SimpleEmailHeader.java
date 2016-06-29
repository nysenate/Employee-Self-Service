package gov.nysenate.ess.core.service.notification.email.simple.header;

import gov.nysenate.ess.core.service.notification.NotificationUtils;
import gov.nysenate.ess.core.service.notification.base.header.castType.Unicast;
import gov.nysenate.ess.core.service.notification.base.header.eventType.Simple;
import gov.nysenate.ess.core.service.notification.base.header.notificationType.Normal;

import java.util.Map;

/**
 *  Simple mail is an unicast simple normal message
 * Created by Chenguang He on 6/14/2016.
 */
public class SimpleEmailHeader implements Unicast, Simple, Normal {
    @Override
    public String getHeaderName() {
        return NotificationUtils.getHeaderName(castType, eventType, notificationType);
    }

    @Override
    public String getHeaderValue() {
        return NotificationUtils.getHeaderValue(castType, eventType, notificationType);
    }

    @Override
    public Map<String, String> toMap() {
        return NotificationUtils.toMap(castType, eventType, notificationType);
    }
}
