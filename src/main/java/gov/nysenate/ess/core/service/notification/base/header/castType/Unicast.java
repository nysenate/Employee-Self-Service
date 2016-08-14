package gov.nysenate.ess.core.service.notification.base.header.castType;

/**
 * Created by Chenguang He  on 6/15/2016.
 * An event is a unicast event, if and only if the event send to only one subscriber
 */
public interface Unicast extends CastType {
    StringBuilder castType = new StringBuilder(CastType.castType).append("." + Unicast.class.getSimpleName());
}
