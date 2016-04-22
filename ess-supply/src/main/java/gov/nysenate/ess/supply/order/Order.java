package gov.nysenate.ess.supply.order;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.supply.item.LineItem;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

public class Order {

    private final int id;
    private final OrderHistory orderHistory;

    private Order(int id, OrderHistory orderHistory) {
        this.id = id;
        this.orderHistory = orderHistory;
    }

    /** Static constructors */

    public static Order of(int id, OrderHistory orderHistory) {
        return new Order(id, orderHistory);
    }

    /** Functional Methods */

    public Order addVersion(OrderVersion version, LocalDateTime modifiedDateTime) {
        return Order.of(this.id, orderHistory.addVersion(modifiedDateTime, version));
    }

    public Order approveOrder(Employee modifiedEmp, LocalDateTime modifiedDateTime) {
        OrderVersion newVersion = current()
                .setStatus(OrderStatus.APPROVED)
                .setModifiedBy(modifiedEmp);
        return Order.of(this.id, orderHistory.addVersion(modifiedDateTime, newVersion));
    }

    public OrderHistory getHistory() {
        return orderHistory;
    }

    public LocalDateTime getOrderedDateTime() {
        return orderHistory.getOrderedDateTime();
    }

    public LocalDateTime getModifiedDateTime() {
        return orderHistory.getModifiedDateTime();
    }

    public OrderVersion current() {
        return orderHistory.current();
    }

    /** Get id returns the order id, not the current version id like other getters. */
    public int getId() {
        return this.id;
    }

    /** Getters, get values from current version. */

    public Employee getCustomer() {
        return current().getCustomer();
    }

    public Location getDestination() {
        return current().getDestination();
    }

    public OrderStatus getStatus() {
        return current().getStatus();
    }

    public ImmutableSet<LineItem> getLineItems() {
        return current().getLineItems();
    }

    public Employee getModifiedBy() {
        return current().getModifiedBy();
    }

    public Optional<String> getNote() {
        return current().getNote();
    }

    /** Internal methods */

    @Override
    public String toString() {
        return "Order{" +
               "orderHistory=" + orderHistory +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return !(orderHistory != null ? !orderHistory.equals(order.orderHistory) : order.orderHistory != null);
    }

    @Override
    public int hashCode() {
        return orderHistory != null ? orderHistory.hashCode() : 0;
    }
}
