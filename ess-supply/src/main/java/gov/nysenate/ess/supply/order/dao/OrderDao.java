package gov.nysenate.ess.supply.order.dao;

import gov.nysenate.ess.supply.order.Order;

import java.util.List;

public interface OrderDao {

    int getUniqueId();

    void saveOrder(Order order);

    List<Order> getOrders();

    Order getOrderById(int orderId);
}
