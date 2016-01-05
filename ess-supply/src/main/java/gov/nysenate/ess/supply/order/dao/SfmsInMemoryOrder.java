package gov.nysenate.ess.supply.order.dao;

import com.google.common.collect.Range;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Repository
public class SfmsInMemoryOrder {

    private Map<Integer, Order> orders = new TreeMap<>();

    public SfmsInMemoryOrder() {
        reset();
    }

    public void reset() {
        orders = new TreeMap<>();
    }

    public int getUniqueId() {
        return 0;
    }

    public void saveOrder(Order order) {
        orders.put(order.getId(), order);
    }

    public List<Order> getOrders(EnumSet<OrderStatus> statuses, Range<LocalDate> dateRange) {
        return new ArrayList<>(orders.values());
    }

    public Order getOrderById(int orderId) {
        return null;
    }

    public void undoCompletion(Order order) {

    }
}
