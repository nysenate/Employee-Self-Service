package gov.nysenate.ess.supply.order;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.supply.item.LineItem;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class Order {

    private final int id;
    private SortedMap<LocalDateTime, OrderVersion> orderVersionMap;

    private Order(int id, SortedMap<LocalDateTime, OrderVersion> versionMap) {
        this.id = id;
        this.orderVersionMap = versionMap;
    }

    /** Static constructors */

    public static Order newOrder(int id, OrderVersion version, LocalDateTime createdDateTime) {
        SortedMap<LocalDateTime, OrderVersion> versionMap = new TreeMap<>();
        versionMap.put(createdDateTime, version);
        return new Order(id, versionMap);
    }

    /** Functional Methods */

    public void rejectOrder(String note, Employee modifiedEmp, LocalDateTime modifiedDateTime) {
        OrderVersion rejected = current()
                .setId(getNewVersionId())
                .setStatus(OrderStatus.REJECTED)
                .setNote(note)
                .setModifiedBy(modifiedEmp);
        orderVersionMap.put(modifiedDateTime, rejected);
    }

    public void updateLineItems(Set<LineItem> lineItems, String note, Employee modifiedEmp, LocalDateTime modifiedDateTime) {
        OrderVersion updated = current()
                .setId(getNewVersionId())
                .setLineItems(lineItems)
                .setNote(note)
                .setModifiedBy(modifiedEmp);
        orderVersionMap.put(modifiedDateTime, updated);
    }

    public SortedMap<LocalDateTime, OrderVersion> getVersions() {
        return new TreeMap<>(orderVersionMap);
    }

    public LocalDateTime getOrderedDateTime() {
        return orderVersionMap.firstKey();
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

    private OrderVersion current() {
        return orderVersionMap.get(orderVersionMap.lastKey());
    }

    private int getNewVersionId() {
        return this.orderVersionMap.size() + 1;
    }

}
