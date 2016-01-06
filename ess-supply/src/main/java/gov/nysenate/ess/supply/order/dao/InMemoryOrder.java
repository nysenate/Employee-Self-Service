package gov.nysenate.ess.supply.order.dao;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryOrder implements OrderDao {

    private Map<Integer, Order> orderDB = new TreeMap<>();

    public InMemoryOrder() {
        reset();
    }

    public void reset() {
        orderDB = new TreeMap<>();
    }

    @Override
    public int getUniqueId() {
        return orderDB.size() + 1;
    }

    @Override
    public void saveOrder(Order order) {
        orderDB.put(order.getId(), order);
    }

    @Override
    public List<Order> getOrders(EnumSet<OrderStatus> statuses, Range<LocalDate> dateRange, LimitOffset limOff) {
        return orderDB.values().stream().filter(order -> statuses.contains(order.getStatus())).collect(Collectors.toList());
    }

    @Override
    public Order getOrderById(int orderId) {
        return orderDB.get(orderId);
    }

    @Override
    public void undoCompletion(Order order) {

    }

}
