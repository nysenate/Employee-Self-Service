package gov.nysenate.ess.supply.requisition;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.supply.item.LineItem;

import java.util.HashSet;
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
    private final Employee modifiedBy;
    private final String note;

    private RequisitionVersion(Builder builder) {
        checkArgument(builder.lineItems.size() > 0, "Requisition Version must contain at least one line item.");
        this.id = builder.id;
        this.customer = checkNotNull(builder.customer, "Requisition Version requires non null customer.");
        this.destination = checkNotNull(builder.destination, "Requisition Version requires non null destination.");
        this.lineItems = ImmutableSet.copyOf(builder.lineItems);
        this.status = checkNotNull(builder.status, "Requisition Version requires non null status.");
        this.issuer = builder.issuer;
        this.modifiedBy = builder.modifiedBy;
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

    public RequisitionStatus getStatus() {
        return status;
    }

    public Optional<Employee> getIssuer() {
        return Optional.ofNullable(issuer);
    }

    public Employee getModifiedBy() {
        return modifiedBy;
    }

    public Optional<String> getNote() {
        return Optional.ofNullable(note);
    }

    @Override
    public String toString() {
        return "RequisitionVersion{" +
               "id=" + id +
               ", customer=" + customer +
               ", destination=" + destination +
               ", lineItems=" + lineItems +
               ", status=" + status +
               ", issuer=" + issuer +
               ", modifiedBy=" + modifiedBy +
               ", note='" + note + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RequisitionVersion that = (RequisitionVersion) o;

        if (id != that.id) return false;
        if (customer != null ? !customer.equals(that.customer) : that.customer != null) return false;
        if (destination != null ? !destination.equals(that.destination) : that.destination != null) return false;
        if (lineItems != null ? !lineItems.equals(that.lineItems) : that.lineItems != null) return false;
        if (status != that.status) return false;
        if (issuer != null ? !issuer.equals(that.issuer) : that.issuer != null) return false;
        if (modifiedBy != null ? !modifiedBy.equals(that.modifiedBy) : that.modifiedBy != null) return false;
        return note != null ? note.equals(that.note) : that.note == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (customer != null ? customer.hashCode() : 0);
        result = 31 * result + (destination != null ? destination.hashCode() : 0);
        result = 31 * result + (lineItems != null ? lineItems.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (issuer != null ? issuer.hashCode() : 0);
        result = 31 * result + (modifiedBy != null ? modifiedBy.hashCode() : 0);
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
        private Employee modifiedBy;
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

        public Builder withModifiedBy(Employee modifiedBy) {
            this.modifiedBy = modifiedBy;
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
