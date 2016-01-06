package gov.nysenate.ess.supply.order.service;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public interface OrderService {

    Order getOrderById(int orderId);

    List<Order> getOrders(EnumSet<OrderStatus> statuses, Range<LocalDate> dateRange, LimitOffset limOff);

    Order submitOrder(int empId, Set<LineItem> items);

    void saveOrder(Order order);

    Order processOrder(int orderId, int issuingEmpId);

    Order completeOrder(int orderId);

    Order undoCompletion(int id);

    Order rejectOrder(int orderId);
}
