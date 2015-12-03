package gov.nysenate.ess.supply.order.dao;

import gov.nysenate.ess.supply.order.Order;

public interface OrderDao {

    int getUniqueId();

    void saveOrder(Order order);

    Order getOrderById(int orderId);
}
