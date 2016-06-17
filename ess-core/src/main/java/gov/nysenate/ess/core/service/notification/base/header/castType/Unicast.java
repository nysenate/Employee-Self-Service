package gov.nysenate.ess.core.service.notification.base.header.castType;

/**
 * An event is a unicast event, if and only if the event send to only one subscriber
 */
public interface Unicast extends CastType {
    StringBuilder castType = new StringBuilder(CastType.castType).append("." + Unicast.class.getSimpleName());
}
