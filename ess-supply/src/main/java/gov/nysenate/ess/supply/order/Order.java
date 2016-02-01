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
    private final Set<LineItem> lineItems;

    public static class Builder {
        private int id;
        private Employee customer;
        private LocalDateTime orderDateTime;
        private Location location;
        private Employee issuingEmployee;
        private LocalDateTime processedDateTime;
        private LocalDateTime completedDateTime;
        private OrderStatus status;
        private Set<LineItem> lineItems;

        public Builder(int id, Employee customer, LocalDateTime orderDateTime, Location location, OrderStatus status) {
            this.id = id;
            this.customer = customer;
            this.orderDateTime = orderDateTime;
            this.location = location;
            this.status = status;
            this.issuingEmployee = null;
            this.processedDateTime = null;
            this.completedDateTime = null;
            this.lineItems = null;
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
        public Builder lineItems(Set<LineItem> lineItems) {
            this.lineItems = lineItems;
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
        this.lineItems = builder.lineItems;
    }

    private Builder copy() {
        return new Builder(id, customer, orderDateTime, location, status)
                .issuingEmployee(issuingEmployee)
                .processedDateTime(processedDateTime)
                .completedDateTime(completedDateTime)
                .lineItems(lineItems);
    }

    public String getLocationCode() {
        return this.location.getCode();
    }

    public String getLocationType() {
        return String.valueOf(this.location.getType().getCode());
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

    public Set<LineItem> getLineItems() {
        return lineItems;
    }

    public Order setLineItems(Set<LineItem> lineItems) {
        return copy().lineItems(lineItems).build();
    }

    @Override
    public String toString() {
        return "Order{" +
               "id=" + id +
               ", customer=" + customer +
               ", orderDateTime=" + orderDateTime +
               ", location=" + location +
               ", issuingEmployee=" + issuingEmployee +
               ", processedDateTime=" + processedDateTime +
               ", completedDateTime=" + completedDateTime +
               ", status=" + status +
               ", lineItems=" + lineItems +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (id != order.id) return false;
        if (!customer.equals(order.customer)) return false;
        if (!orderDateTime.equals(order.orderDateTime)) return false;
        if (!location.equals(order.location)) return false;
        if (issuingEmployee != null ? !issuingEmployee.equals(order.issuingEmployee) : order.issuingEmployee != null)
            return false;
        if (processedDateTime != null ? !processedDateTime.equals(order.processedDateTime) : order.processedDateTime != null)
            return false;
        if (completedDateTime != null ? !completedDateTime.equals(order.completedDateTime) : order.completedDateTime != null)
            return false;
        if (status != order.status) return false;
        return !(lineItems != null ? !lineItems.equals(order.lineItems) : order.lineItems != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + customer.hashCode();
        result = 31 * result + orderDateTime.hashCode();
        result = 31 * result + location.hashCode();
        result = 31 * result + (issuingEmployee != null ? issuingEmployee.hashCode() : 0);
        result = 31 * result + (processedDateTime != null ? processedDateTime.hashCode() : 0);
        result = 31 * result + (completedDateTime != null ? completedDateTime.hashCode() : 0);
        result = 31 * result + status.hashCode();
        result = 31 * result + (lineItems != null ? lineItems.hashCode() : 0);
        return result;
    }
}
