package gov.nysenate.ess.supply.order.dao;

import com.google.common.collect.Range;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderStatus;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;

public interface OrderDao {

    int getUniqueId();

    void saveOrder(Order order);

    List<Order> getOrders(EnumSet<OrderStatus> statuses, Range<LocalDate> dateRange);

    Order getOrderById(int orderId);

    void undoCompletion(Order order);
}
