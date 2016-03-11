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

    public static Order newOrder(int id, OrderHistory orderHistory) {
        return new Order(id, orderHistory);
    }

    /** Functional Methods */

    public Order rejectOrder(String note, Employee modifiedEmp, LocalDateTime modifiedDateTime) {
        OrderVersion rejected = current()
                .setId(getNewVersionId())
                .setStatus(OrderStatus.REJECTED)
                .setNote(note)
                .setModifiedBy(modifiedEmp);
        return new Order(this.id, orderHistory.addVersion(modifiedDateTime, rejected));
    }

    public void updateLineItems(Set<LineItem> lineItems, String note, Employee modifiedEmp, LocalDateTime modifiedDateTime) {
//        OrderVersion updated = current()
//                .setId(getNewVersionId())
//                .setLineItems(lineItems)
//                .setNote(note)
//                .setModifiedBy(modifiedEmp);
//        orderVersionMap.put(modifiedDateTime, updated);
    }

    public OrderHistory getHistory() {
        return orderHistory;
    }

//    public LocalDateTime getOrderedDateTime() {
//        return orderVersionMap.firstKey();
//    }

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

    private OrderVersion current() {
        return orderHistory.current();
    }

    private int getNewVersionId() {
        return orderHistory.size() + 1;
    }

}
