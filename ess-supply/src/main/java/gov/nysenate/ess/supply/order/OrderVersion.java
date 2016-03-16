package gov.nysenate.ess.supply.order;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.supply.item.LineItem;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class OrderVersion {

    private final int id;
    private final Employee customer;
    private final Location destination;
    private final OrderStatus status;
    private final ImmutableSet<LineItem> lineItems;
    private final Employee modifiedBy;
    private final String note;

    /** Must be constructed through {@link OrderVersion.Builder}. */
    private OrderVersion(Builder builder) {
        this.id = builder.id;
        this.customer = builder.customer;
        this.destination = builder.destination;
        this.status = builder.status;
        this.lineItems = ImmutableSet.copyOf(builder.lineItems);
        this.modifiedBy = builder.modifiedBy;
        this.note = builder.note;
    }

    private Builder copy() {
        return new Builder()
                .withId(id)
                .withCustomer(customer)
                .withDestination(destination)
                .withStatus(status)
                .withLineItems(lineItems)
                .withModifiedBy(modifiedBy)
                .withNote(note);
    }

    /** Function Methods **/

    public OrderVersion addLineItem(LineItem lineItem) {
        return copy().addLineItem(lineItem).build();
    }

    public String getDestinationLocCode() {
        return this.destination.getCode();
    }

    public String getDestinationLocType() {
        return String.valueOf(this.destination.getType().getCode());
    }

    /** Basic Setters. Return new instances. */

    public OrderVersion setId(int id) {
        return copy().withId(id).build();
    }

    public OrderVersion setStatus(OrderStatus status) {
        return copy().withStatus(status).build();
    }

    public OrderVersion setLineItems(Set<LineItem> lineItems) {
        return copy().withLineItems(lineItems).build();
    }

    public OrderVersion setModifiedBy(Employee modifiedBy) {
        return copy().withModifiedBy(modifiedBy).build();
    }

    public OrderVersion setNote(String note) {
        return copy().withNote(note).build();
    }

    /** Basic Getters */

    public int getId() {
        return id;
    }

    public Employee getCustomer() {
        return customer;
    }

    public Location getDestination() {
        return destination;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public ImmutableSet<LineItem> getLineItems() {
        return lineItems;
    }

    public Employee getModifiedBy() {
        return modifiedBy;
    }

    public Optional<String> getNote() {
        return Optional.ofNullable(note);
    }

    @Override
    public String toString() {
        return "OrderVersion{" +
               "customer=" + customer +
               ", destination=" + destination +
               ", status=" + status +
               ", lineItems=" + lineItems +
               ", modifiedBy=" + modifiedBy +
               ", note='" + note + '\'' +
               '}';
    }

    /**
     * Does not use id in equality. Therefore two distinct order versions from a business perspective
     * can be equal according to this method. If you don't want that, check equality on {@link Order} or {@link OrderHistory}.
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderVersion that = (OrderVersion) o;

        if (customer != null ? !customer.equals(that.customer) : that.customer != null) return false;
        if (destination != null ? !destination.equals(that.destination) : that.destination != null) return false;
        if (status != that.status) return false;
        if (lineItems != null ? !lineItems.equals(that.lineItems) : that.lineItems != null) return false;
        if (modifiedBy != null ? !modifiedBy.equals(that.modifiedBy) : that.modifiedBy != null) return false;
        return !(note != null ? !note.equals(that.note) : that.note != null);
    }

    @Override
    public int hashCode() {
        int result = customer != null ? customer.hashCode() : 0;
        result = 31 * result + (destination != null ? destination.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (lineItems != null ? lineItems.hashCode() : 0);
        result = 31 * result + (modifiedBy != null ? modifiedBy.hashCode() : 0);
        result = 31 * result + (note != null ? note.hashCode() : 0);
        return result;
    }

    /** Inner Builder class used to construct Order instances. */
    public static class Builder {
        private int id;
        private Employee customer;
        private Location destination;
        private OrderStatus status;
        private Set<LineItem> lineItems;
        private Employee modifiedBy;
        private String note;

        public Builder() {
            this.lineItems = new HashSet<>();
        }

        public Builder withId(int id) {
            this.id = id;
            return this;
        }

        public Builder withCustomer(Employee customer) {
            this.customer = customer;
            return this;
        }

        public Builder withDestination(Location destination) {
            this.destination = destination;
            return this;
        }

        public Builder withStatus(OrderStatus status) {
            this.status = status;
            return this;
        }

        public Builder withLineItems(Set<LineItem> lineItems) {
            this.lineItems = lineItems;
            return this;
        }

        public Builder addLineItem(LineItem lineItem) {
            this.lineItems.add(lineItem);
            return this;
        }

        public Builder withModifiedBy(Employee modifiedBy) {
            this.modifiedBy = modifiedBy;
            return this;
        }

        public Builder withNote(String note) {
            this.note = note;
            return this;
        }

        public OrderVersion build() {
            return new OrderVersion(this);
        }
    }
}
