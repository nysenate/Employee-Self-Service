package gov.nysenate.ess.supply.order.service;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.supply.order.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OrderService {

    Order submitOrder(Employee customer, Location location, Map<Integer, Integer> items);

    Order getOrderById(int orderId);

    Order updateOrderItems(int orderId, Map<Integer, Integer> newItems);

    Order rejectOrder(int orderId);

    Order processOrder(int orderId, Employee issuingEmployee);

    List<Order> getOrders();

    List<Order> getPendingOrders();

    List<Order> getProcessingOrders();

    Order completeOrder(int orderId);

    List<Order> getCompletedOrders();

    List<Order> getCompletedOrdersBetween(LocalDateTime start, LocalDateTime end);
}
