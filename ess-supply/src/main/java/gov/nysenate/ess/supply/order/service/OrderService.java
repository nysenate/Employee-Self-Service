package gov.nysenate.ess.supply.order.service;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.supply.order.Order;

import java.util.List;
import java.util.Map;

public interface OrderService {

    int submitOrder(Employee customer, Location location, Map<Integer, Integer> items);

    Order getOrderById(int orderId);

    void updateOrderItems(int orderId, Map<Integer, Integer> newItems);

    void rejectOrder(int orderId);

    void processOrder(int orderId, Employee issuingEmployee);

    List<Order> getOrders();

    List<Order> getPendingOrders();

    List<Order> getProcessingOrders();

}
