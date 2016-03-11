package gov.nysenate.ess.supply.order.dao;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderStatus;
import gov.nysenate.ess.supply.order.OrderVersion;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InMemoryOrderDao implements OrderDao {

    private Map<Integer, Order> orders;

    public InMemoryOrderDao() {
        this.orders = new HashMap<>();
    }

    @Override
    public Order insertOrder(OrderVersion version, LocalDateTime modifyDateTime) {
        Order order = Order.newOrder(orders.size() + 1, version, modifyDateTime);
        orders.put(order.getId(), order);
        return order;
    }

    @Override
    public void saveOrder(Order order) {
        orders.put(order.getId(), order);
    }

    @Override
    public Order getOrderById(int orderId) {
        return orders.get(orderId);
    }

    @Override
    public PaginatedList<Order> getOrders(String locCode, String locType, String issuerEmpId, EnumSet<OrderStatus> statuses, Range<LocalDateTime> dateTimeRange, LimitOffset limOff) {
        return null;
    }

    @Override
    public Set<Order> getOrderHistory(int orderId) {
        return null;
    }
}
