package gov.nysenate.ess.supply.order.service;

import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.order.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface OrderService {

    Order submitOrder(int empId, Set<LineItem> items);

    Order getOrderById(int orderId);

    void saveOrder(Order order);

    Order rejectOrder(int orderId);

    Order processOrder(int orderId, int issuingEmpId);

    List<Order> getOrders();

    List<Order> getPendingOrders();

    List<Order> getProcessingOrders();

    Order completeOrder(int orderId);

    List<Order> getCompletedOrders();

    List<Order> getCompletedOrdersBetween(LocalDateTime start, LocalDateTime end);

    Order undoCompletion(int id);
}
