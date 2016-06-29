package gov.nysenate.ess.core.service.notification.base.header.castType;

/**
 * An event is a broadcast event, if and only if the event send to more than one subscriber
 * Created by Chenguang He  on 6/15/2016.
 */
public interface Broadcast extends CastType {
    StringBuilder castType = new StringBuilder(CastType.castType).append("." + Broadcast.class.getName());
}
