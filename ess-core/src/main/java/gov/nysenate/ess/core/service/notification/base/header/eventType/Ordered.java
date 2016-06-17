package gov.nysenate.ess.core.service.notification.base.header.eventType;

/**
 * Created by senateuser on 6/14/2016.
 */
public interface Ordered extends EventType {
    StringBuilder eventType = new StringBuilder(EventType.eventType).append("." + Ordered.class.getName());

    Order getOrder();

    void setOrder(Order order);

    enum Order {
        byTime, byAphbet
    }
}
