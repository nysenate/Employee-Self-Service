package gov.nysenate.ess.supply.order.dao;

import gov.nysenate.ess.supply.order.Order;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Repository
public class SfmsInMemoryOrder implements OrderDao {

    private Map<Integer, Order> orders = new TreeMap<>();

    public SfmsInMemoryOrder() {
        reset();
    }

    public void reset() {
        orders = new TreeMap<>();
    }

    @Override
    public int getUniqueId() {
        return 0;
    }

    @Override
    public void saveOrder(Order order) {
        orders.put(order.getId(), order);
    }

    @Override
    public List<Order> getOrders() {
        return new ArrayList<>(orders.values());
    }

    @Override
    public Order getOrderById(int orderId) {
        return null;
    }

    @Override
    public void undoCompletion(Order order) {

    }
}
