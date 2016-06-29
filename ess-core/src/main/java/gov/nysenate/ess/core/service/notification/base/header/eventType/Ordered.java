package gov.nysenate.ess.core.service.notification.base.header.eventType;

/**
 * an message is ordered if and only if the message has a comparator and can sorted in certain way.
 * Created by Chenguang He  on 6/15/2016.
 */
public interface Ordered extends EventType {
    StringBuilder eventType = new StringBuilder(EventType.eventType).append("." + Ordered.class.getName());

    /**
     * get the order
     *
     * @return the order
     */
    Order getOrder();

    /**
     * set the order
     * @param order the order
     */
    void setOrder(Order order);

    /**
     * enum class to represent the order.
     */
    enum Order {
        byTime, byAphbet
    }
}
