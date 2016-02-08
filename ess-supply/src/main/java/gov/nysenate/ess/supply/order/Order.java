package gov.nysenate.ess.supply.order;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.supply.item.LineItem;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
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
    /** Audit fields */
    private final int modifiedEmpId;
    private final LocalDateTime modifiedDateTime;

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
        private int modifiedEmpId;
        private LocalDateTime modifiedDateTime;

        public Builder(Employee customer, LocalDateTime orderDateTime, Location location, OrderStatus status,
                       int modifiedEmpId, LocalDateTime modifiedDateTime) {
            this.id = 0;
            this.customer = customer;
            this.orderDateTime = orderDateTime;
            this.location = location;
            this.status = status;
            this.issuingEmployee = null;
            this.processedDateTime = null;
            this.completedDateTime = null;
            this.lineItems = new HashSet<>();
            this.modifiedEmpId = modifiedEmpId;
            this.modifiedDateTime = modifiedDateTime;
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder status(OrderStatus status) {
            this.status = status;
            return this;
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

        public Builder lineItems(Set<LineItem> lineItems) {
            this.lineItems = lineItems;
            return this;
        }

        public Builder addLineItem(LineItem lineItem) {
            this.lineItems.add(lineItem);
            return this;
        }

        public Builder setModifiedEmpId(int modifiedEmpId) {
            this.modifiedEmpId = modifiedEmpId;
            return this;
        }

        public Builder setModifiedDateTime(LocalDateTime modifiedDateTime) {
            this.modifiedDateTime = modifiedDateTime;
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
        this.modifiedEmpId = builder.modifiedEmpId;
        this.modifiedDateTime = builder.modifiedDateTime;
    }

    private Builder copy() {
        return new Builder(customer, orderDateTime, location, status, modifiedEmpId, modifiedDateTime)
                .id(id)
                .issuingEmployee(issuingEmployee)
                .processedDateTime(processedDateTime)
                .completedDateTime(completedDateTime)
                .lineItems(lineItems);
    }

    /** Function Methods **/

    public Order addLineItem(LineItem lineItem) {
        return copy().addLineItem(lineItem).build();
    }

    public String getLocationCode() {
        return this.location.getCode();
    }

    public String getLocationType() {
        return String.valueOf(this.location.getType().getCode());
    }

    /** Basic get/set methods. Nullable fields return an Optional. **/

    public int getId() {
        return id;
    }

    public Order setId(int id) {
        return copy().id(id).build();
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

    public Optional<Employee> getIssuingEmployee() {
        return Optional.ofNullable(issuingEmployee);
    }

    public Order setIssuingEmployee(Employee employee) {
        return copy().issuingEmployee(employee).build();
    }

    public Optional<LocalDateTime> getProcessedDateTime() {
        return Optional.ofNullable(processedDateTime);
    }

    public Order setProcessedDateTime(LocalDateTime dateTime) {
        return copy().processedDateTime(dateTime).build();
    }

    public Optional<LocalDateTime> getCompletedDateTime() {
        return Optional.ofNullable(completedDateTime);
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

    public int getModifiedEmpId() {
        return modifiedEmpId;
    }

    public Order setModifiedEmpId(int modifiedEmpId) {
        return copy().setModifiedEmpId(modifiedEmpId).build();
    }

    public LocalDateTime getModifiedDateTime() {
        return modifiedDateTime;
    }

    public Order setModifiedDateTime(LocalDateTime dateTime) {
        return copy().setModifiedDateTime(dateTime).build();
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
               ", modifiedEmpId=" + modifiedEmpId +
               ", modifiedDateTime=" + modifiedDateTime +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (id != order.id) return false;
        if (modifiedEmpId != order.modifiedEmpId) return false;
        if (customer != null ? !customer.equals(order.customer) : order.customer != null) return false;
        if (orderDateTime != null ? !orderDateTime.equals(order.orderDateTime) : order.orderDateTime != null)
            return false;
        if (location != null ? !location.equals(order.location) : order.location != null) return false;
        if (issuingEmployee != null ? !issuingEmployee.equals(order.issuingEmployee) : order.issuingEmployee != null)
            return false;
        if (processedDateTime != null ? !processedDateTime.equals(order.processedDateTime) : order.processedDateTime != null)
            return false;
        if (completedDateTime != null ? !completedDateTime.equals(order.completedDateTime) : order.completedDateTime != null)
            return false;
        if (status != order.status) return false;
        if (lineItems != null ? !lineItems.equals(order.lineItems) : order.lineItems != null) return false;
        return !(modifiedDateTime != null ? !modifiedDateTime.equals(order.modifiedDateTime) : order.modifiedDateTime != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (customer != null ? customer.hashCode() : 0);
        result = 31 * result + (orderDateTime != null ? orderDateTime.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (issuingEmployee != null ? issuingEmployee.hashCode() : 0);
        result = 31 * result + (processedDateTime != null ? processedDateTime.hashCode() : 0);
        result = 31 * result + (completedDateTime != null ? completedDateTime.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (lineItems != null ? lineItems.hashCode() : 0);
        result = 31 * result + modifiedEmpId;
        result = 31 * result + (modifiedDateTime != null ? modifiedDateTime.hashCode() : 0);
        return result;
    }
}
