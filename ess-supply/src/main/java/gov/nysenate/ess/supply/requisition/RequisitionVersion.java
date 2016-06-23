package gov.nysenate.ess.supply.requisition;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.supply.item.LineItem;

import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class RequisitionVersion {
    private final int id;
    private final Employee customer;
    private final Location destination;
    private final ImmutableSet<LineItem> lineItems;
    private final RequisitionStatus status;
    private final Employee issuer;
    private final Employee createdBy;
    private final String note;

    private RequisitionVersion(Builder builder) {
        checkArgument(builder.lineItems.size() > 0, "Requisition Version must contain at least one line item.");
        this.id = builder.id;
        this.customer = checkNotNull(builder.customer, "Requisition Version requires non null customer.");
        this.destination = checkNotNull(builder.destination, "Requisition Version requires non null destination.");
        this.lineItems = ImmutableSet.copyOf(builder.lineItems);
        this.status = checkNotNull(builder.status, "Requisition Version requires non null status.");
        this.issuer = builder.issuer;
        this.createdBy = checkNotNull(builder.createdBy, "Requisition Version requires created by employee.");
        this.note = builder.note;
    }

    public int getId() {
        return id;
    }

    public Employee getCustomer() {
        return this.customer;
    }

    public Location getDestination() {
        return destination;
    }

    public ImmutableSet<LineItem> getLineItems() {
        return lineItems;
    }

    public String toOrderString() {
        StringBuilder sb = new StringBuilder();
        for (LineItem l : lineItems) {
            sb.append(l.getItem().getCommodityCode() + " x " + l.getQuantity() + "\n");
        }
        return sb.toString();
    }

    public RequisitionStatus getStatus() {
        return status;
    }

    public Optional<Employee> getIssuer() {
        return Optional.ofNullable(issuer);
    }

    public Employee getCreatedBy() {
        return createdBy;
    }

    public Optional<String> getNote() {
        return Optional.ofNullable(note);
    }

    @Override
    public String toString() {
        return "RequisitionVersion{" +
               "customer=" + customer +
               ", destination=" + destination +
               ", lineItems=" + lineItems +
               ", status=" + status +
               ", issuer=" + issuer +
               ", createdBy=" + createdBy +
               ", note='" + note + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequisitionVersion version = (RequisitionVersion) o;
        if (customer != null ? !customer.equals(version.customer) : version.customer != null) return false;
        if (destination != null ? !destination.equals(version.destination) : version.destination != null) return false;
        if (lineItems != null ? !lineItems.equals(version.lineItems) : version.lineItems != null) return false;
        if (status != version.status) return false;
        if (issuer != null ? !issuer.equals(version.issuer) : version.issuer != null) return false;
        if (createdBy != null ? !createdBy.equals(version.createdBy) : version.createdBy != null) return false;
        return note != null ? note.equals(version.note) : version.note == null;
    }

    @Override
    public int hashCode() {
        int result = customer != null ? customer.hashCode() : 0;
        result = 31 * result + (destination != null ? destination.hashCode() : 0);
        result = 31 * result + (lineItems != null ? lineItems.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (issuer != null ? issuer.hashCode() : 0);
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (note != null ? note.hashCode() : 0);
        return result;
    }

    public static class Builder {
        private int id;
        private Employee customer;
        private Location destination;
        private Set<LineItem> lineItems;
        private RequisitionStatus status;
        private Employee issuer;
        private Employee createdBy;
        private String note;

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

        public Builder withLineItems(Set<LineItem> lineItems) {
            this.lineItems = lineItems;
            return this;
        }

        public Builder withStatus(RequisitionStatus status) {
            this.status = status;
            return this;
        }

        public Builder withIssuer(Employee issuer) {
            this.issuer = issuer;
            return this;
        }

        public Builder withCreatedBy(Employee createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder withNote(String note) {
            this.note = note;
            return this;
        }

        public RequisitionVersion build() {
            return new RequisitionVersion(this);
        }

    }
}
