package gov.nysenate.ess.supply.order.dao;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderStatus;
import gov.nysenate.ess.supply.order.OrderVersion;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

public interface OrderDao {

    /**
     * Inserts a new {@link Order} into the data storage. Setting the given {@link OrderVersion}
     * as its only version.
     *
     * Creates a unique id for the <code>Order</code> and <code>OrderVersion</code>.
     *
     * @param version The OrderVersion to be saved into a new Order.
     * @param modifyDateTime The date time this version was made.
     * @return The unique id of the created Order.
     */
    int insertOrder(OrderVersion version, LocalDateTime modifyDateTime);

    void saveOrder(Order order);

    Order getOrderById(int orderId);

    PaginatedList<Order> getOrders(String location, String customerId, EnumSet<OrderStatus> statuses,
                                   Range<LocalDateTime> updatedDateTimeRange, LimitOffset limOff);
}
