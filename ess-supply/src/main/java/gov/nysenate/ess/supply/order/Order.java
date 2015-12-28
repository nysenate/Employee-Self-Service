package gov.nysenate.ess.supply.order;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.supply.item.LineItem;

import java.time.LocalDateTime;
import java.util.Set;

public final class Order {

    private final int id;
    private final Employee customer;
    private final LocalDateTime orderDateTime;
    private final Location location;
    private final Employee issuingEmployee;
    private final LocalDateTime processedDateTime;
    private final LocalDateTime completedDateTime;
    private final OrderStatus status;
    private final Set<LineItem> items;

    public static class Builder {
        private int id;
        private Employee customer;
        private LocalDateTime orderDateTime;
        private Location location;
        private Employee issuingEmployee;
        private LocalDateTime processedDateTime;
        private LocalDateTime completedDateTime;
        private OrderStatus status;
        private Set<LineItem> items;

        public Builder(int id, Employee customer, LocalDateTime orderDateTime, Location location, OrderStatus status) {
            this.id = id;
            this.customer = customer;
            this.orderDateTime = orderDateTime;
            this.location = location;
            this.status = status;
            this.issuingEmployee = null;
            this.processedDateTime = null;
            this.completedDateTime = null;
            this.items = null;
        }

        public Builder issuingEmployee(Employee issuingEmployee) {
            this.issuingEmployee = issuingEmployee;
            return this;
        }

        public Builder processedDateTime(LocalDateTime processedDateTime) {
            this.processedDateTime = processedDateTime;
            return this;
        }

        public Builder completedDateTime(LocalDateTime completedDateTime) {
            this.completedDateTime = completedDateTime;
            return this;
        }
        public Builder status(OrderStatus status) {
            this.status = status;
            return this;
        }
        public Builder items(Set<LineItem> items) {
            this.items = items;
            return this;
        }

        public Order build() {
            return new Order(this);
        }

    }

    public Order(Builder builder) {
        this.id = builder.id;
        this.customer = builder.customer;
        this.orderDateTime = builder.orderDateTime;
        this.location = builder.location;
        this.issuingEmployee = builder.issuingEmployee;
        this.processedDateTime = builder.processedDateTime;
        this.completedDateTime = builder.completedDateTime;
        this.status = builder.status;
        this.items = builder.items;
    }

    private Builder copy() {
        return new Builder(id, customer, orderDateTime, location, status)
                .issuingEmployee(issuingEmployee)
                .processedDateTime(processedDateTime)
                .completedDateTime(completedDateTime)
                .items(items);
    }

    public int getId() {
        return id;
    }

    public Employee getCustomer() {
        return customer;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }

    public Location getLocation() {
        return location;
    }

    public Employee getIssuingEmployee() {
        return issuingEmployee;
    }

    public Order setIssuingEmployee(Employee employee) {
        return copy().issuingEmployee(employee).build();
    }

    public LocalDateTime getProcessedDateTime() {
        return processedDateTime;
    }

    public Order setProcessedDateTime(LocalDateTime dateTime) {
        return copy().processedDateTime(dateTime).build();
    }

    public LocalDateTime getCompletedDateTime() {
        return completedDateTime;
    }

    public Order setCompletedDateTime(LocalDateTime dateTime) {
        return copy().completedDateTime(dateTime).build();
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Order setStatus(OrderStatus status) {
        return copy().status(status).build();
    }

    public Set<LineItem> getItems() {
        return items;
    }

    public Order setItems(Set<LineItem> items) {
        return copy().items(items).build();
    }
}
