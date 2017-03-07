package gov.nysenate.ess.supply.requisition.model;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.supply.item.LineItem;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Requisition {
    private final int requisitionId;
    private final int revisionId;
    private final Employee customer;
    private final Location destination;
    private final ImmutableSet<LineItem> lineItems;
    private final String specialInstructions; // instructions left by the customer.
    private final RequisitionState state;
    private final RequisitionStatus status;
    private final Employee issuer;
    private final String note; // Note/comment made by supply on a requisition.
    private final Employee modifiedBy;
    private final LocalDateTime modifiedDateTime;
    private final LocalDateTime orderedDateTime;
    private final LocalDateTime processedDateTime;
    private final LocalDateTime completedDateTime;
    private final LocalDateTime approvedDateTime;
    private final LocalDateTime rejectedDateTime;
    private final LocalDateTime lastSfmsSyncDateTime;
    private final boolean savedInSfms;

    private Requisition(Builder builder) {
        this.requisitionId = builder.requisitionId;
        this.revisionId = builder.revisionId;
        this.customer = checkNotNull(builder.customer, "Requisition requires non null customer.");
        this.destination = checkNotNull(builder.destination, "Requisition requires non null destination.");
        this.lineItems = ImmutableSet.copyOf(builder.lineItems);
        this.specialInstructions = builder.specialInstructions;
        this.state = checkNotNull(builder.state, "Requisition requires non null RequisitionState.");
        this.status = checkNotNull(builder.status, "Requisition requires non null status.");
        this.issuer = builder.issuer;
        this.note = builder.note;
        this.modifiedBy = checkNotNull(builder.modifiedBy, "Requisition requires a modified by employee.");
        this.modifiedDateTime = builder.modifiedDateTime;
        this.orderedDateTime = checkNotNull(builder.orderedDateTime, "Requisition requires a ordered date time.");
        this.processedDateTime = builder.processedDateTime;
        this.completedDateTime = builder.completedDateTime;
        this.approvedDateTime = builder.approvedDateTime;
        this.rejectedDateTime = builder.rejectedDateTime;
        this.lastSfmsSyncDateTime = builder.lastSfmsSyncDateTime;
        this.savedInSfms = builder.savedInSfms;
    }

    /**
     * Returns a {@link Requisition.Builder} which contains a copy of
     * this requisitions data. Useful for creating new instances where
     * only a few fields differ.
     */
    private Requisition.Builder copy() {
        return new Requisition.Builder()
                .withRequisitionId(this.requisitionId)
                .withRevisionId(this.revisionId)
                .withCustomer(this.customer)
                .withDestination(this.destination)
                .withLineItems(this.lineItems)
                .withSpecialInstructions(this.specialInstructions)
                .withState(this.state)
                .withStatus(this.status)
                .withIssuer(this.issuer)
                .withNote(this.note)
                .withModifiedBy(this.modifiedBy)
                .withModifiedDateTime(this.modifiedDateTime)
                .withOrderedDateTime(this.orderedDateTime)
                .withProcessedDateTime(this.processedDateTime)
                .withCompletedDateTime(this.completedDateTime)
                .withApprovedDateTime(this.approvedDateTime)
                .withRejectedDateTime(this.rejectedDateTime)
                .withLastSfmsSyncDateTimeDateTime(this.lastSfmsSyncDateTime)
                .withSavedInSfms(this.savedInSfms);
    }

    /**
     * Controls transition of state in a Requisition object.
     */
    public Requisition process(LocalDateTime processedDateTime) {
        return state.process(this, processedDateTime);
    }

    public Requisition reject(LocalDateTime rejectedDateTime) {
        return state.reject(this, rejectedDateTime);
    }

    protected Requisition setState(RequisitionState state) {
        return copy().withState(state).build();
    }

    /**
     * Public Setters
     */

    public Requisition setRequisitionId(int requisitionId) {
        return copy().withRequisitionId(requisitionId).build();
    }

    public Requisition setRevisionId(int revisionId) {
        return copy().withRevisionId(revisionId).build();
    }

    public Requisition setCustomer(Employee customer) {
        return copy().withCustomer(customer).build();
    }

    public Requisition setDestination(Location destination) {
        return copy().withDestination(destination).build();
    }

    public Requisition setLineItems(Set<LineItem> lineItems) {
        return copy().withLineItems(lineItems).build();
    }

    public Requisition setStatus(RequisitionStatus status) {
        return copy().withStatus(status).build();
    }

    public Requisition setIssuer(Employee issuer) {
        return copy().withIssuer(issuer).build();
    }

    public Requisition setNote(String note) {
        return copy().withNote(note).build();
    }

    public Requisition setModifiedBy(Employee modifiedBy) {
        return copy().withModifiedBy(modifiedBy).build();
    }

    /** Modified date time should only be set by the dao layer before saving. */
    public Requisition setModifiedDateTime(LocalDateTime modifiedDateTime) {
        return copy().withModifiedDateTime(modifiedDateTime).build();
    }

    public Requisition setOrderedDateTime(LocalDateTime orderedDateTime) {
        return copy().withOrderedDateTime(orderedDateTime).build();
    }

    public Requisition setProcessedDateTime(LocalDateTime processedDateTime) {
        return copy().withProcessedDateTime(processedDateTime).build();
    }

    public Requisition setCompletedDateTime(LocalDateTime completedDateTime) {
        return copy().withCompletedDateTime(completedDateTime).build();
    }

    public Requisition setApprovedDateTime(LocalDateTime approvedDateTime) {
        return copy().withApprovedDateTime(approvedDateTime).build();
    }

    public Requisition setRejectedDateTime(LocalDateTime rejectedDateTime) {
        return copy().withRejectedDateTime(rejectedDateTime).build();
    }

    public Requisition setLastSfmsSyncDateTimeDateTime(LocalDateTime lastSfmsSyncDateTime) {
        return copy().withLastSfmsSyncDateTimeDateTime(lastSfmsSyncDateTime).build();
    }


    public Requisition setSavedInSfms(boolean savedInSfms) {
        return copy().withSavedInSfms(savedInSfms).build();
    }

    /**
     * Basic Getters
     */

    public int getRequisitionId() {
        return requisitionId;
    }

    public int getRevisionId() {
        return revisionId;
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

    public Optional<String> getSpecialInstructions() {
        return Optional.ofNullable(specialInstructions);
    }

    public RequisitionStatus getStatus() {
        return status;
    }

    public Optional<Employee> getIssuer() {
        return Optional.ofNullable(issuer);
    }

    public Optional<String> getNote() {
        return Optional.ofNullable(note);
    }

    public Employee getModifiedBy() {
        return modifiedBy;
    }

    public Optional<LocalDateTime> getModifiedDateTime() {
        return Optional.ofNullable(modifiedDateTime);
    }

    public LocalDateTime getOrderedDateTime() {
        return orderedDateTime;
    }

    public Optional<LocalDateTime> getProcessedDateTime() {
        return Optional.ofNullable(processedDateTime);
    }

    public Optional<LocalDateTime> getCompletedDateTime() {
        return Optional.ofNullable(completedDateTime);
    }

    public Optional<LocalDateTime> getApprovedDateTime() {
        return Optional.ofNullable(approvedDateTime);
    }

    public Optional<LocalDateTime> getRejectedDateTime() {
        return Optional.ofNullable(rejectedDateTime);
    }

    public Optional<LocalDateTime> getLastSfmsSyncDateTime() {
        return Optional.ofNullable(lastSfmsSyncDateTime);
    }

    public boolean getSavedInSfms() {
        return savedInSfms;
    }

    public String toOrderString() {
        StringBuilder sb = new StringBuilder();
        for (LineItem l : lineItems) {
            sb.append(l.getItem().getCommodityCode() + " x " + l.getQuantity() + "\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Requisition{" +
               "requisitionId=" + requisitionId +
               ", revisionId=" + revisionId +
               ", customer=" + customer +
               ", destination=" + destination +
               ", lineItems=" + lineItems +
               ", status=" + status +
               ", issuer=" + issuer +
               ", note='" + note + '\'' +
               ", specialInstructions='" + specialInstructions + '\'' +
               ", modifiedBy=" + modifiedBy +
               ", modifiedDateTime=" + modifiedDateTime +
               ", orderedDateTime=" + orderedDateTime +
               ", processedDateTime=" + processedDateTime +
               ", completedDateTime=" + completedDateTime +
               ", approvedDateTime=" + approvedDateTime +
               ", rejectedDateTime=" + rejectedDateTime +
               ", lastSfmsSyncDateTime=" + lastSfmsSyncDateTime +
               ", savedInSfms=" + savedInSfms +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Requisition that = (Requisition) o;
        if (requisitionId != that.requisitionId) return false;
        if (revisionId != that.revisionId) return false;
        if (savedInSfms != that.savedInSfms) return false;
        if (customer != null ? !customer.equals(that.customer) : that.customer != null) return false;
        if (destination != null ? !destination.equals(that.destination) : that.destination != null) return false;
        if (lineItems != null ? !lineItems.equals(that.lineItems) : that.lineItems != null) return false;
        if (status != that.status) return false;
        if (issuer != null ? !issuer.equals(that.issuer) : that.issuer != null) return false;
        if (note != null ? !note.equals(that.note) : that.note != null) return false;
        if (specialInstructions != null ? !specialInstructions.equals(that.specialInstructions) : that.specialInstructions != null)
            return false;
        if (modifiedBy != null ? !modifiedBy.equals(that.modifiedBy) : that.modifiedBy != null) return false;
        if (modifiedDateTime != null ? !modifiedDateTime.equals(that.modifiedDateTime) : that.modifiedDateTime != null)
            return false;
        if (orderedDateTime != null ? !orderedDateTime.equals(that.orderedDateTime) : that.orderedDateTime != null)
            return false;
        if (processedDateTime != null ? !processedDateTime.equals(that.processedDateTime) : that.processedDateTime != null)
            return false;
        if (completedDateTime != null ? !completedDateTime.equals(that.completedDateTime) : that.completedDateTime != null)
            return false;
        if (approvedDateTime != null ? !approvedDateTime.equals(that.approvedDateTime) : that.approvedDateTime != null)
            return false;
        if (rejectedDateTime != null ? !rejectedDateTime.equals(that.rejectedDateTime) : that.rejectedDateTime != null)
            return false;
        return lastSfmsSyncDateTime != null ? lastSfmsSyncDateTime.equals(that.lastSfmsSyncDateTime) : that.lastSfmsSyncDateTime == null;
    }

    @Override
    public int hashCode() {
        int result = requisitionId;
        result = 31 * result + revisionId;
        result = 31 * result + (customer != null ? customer.hashCode() : 0);
        result = 31 * result + (destination != null ? destination.hashCode() : 0);
        result = 31 * result + (lineItems != null ? lineItems.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (issuer != null ? issuer.hashCode() : 0);
        result = 31 * result + (note != null ? note.hashCode() : 0);
        result = 31 * result + (specialInstructions != null ? specialInstructions.hashCode() : 0);
        result = 31 * result + (modifiedBy != null ? modifiedBy.hashCode() : 0);
        result = 31 * result + (modifiedDateTime != null ? modifiedDateTime.hashCode() : 0);
        result = 31 * result + (orderedDateTime != null ? orderedDateTime.hashCode() : 0);
        result = 31 * result + (processedDateTime != null ? processedDateTime.hashCode() : 0);
        result = 31 * result + (completedDateTime != null ? completedDateTime.hashCode() : 0);
        result = 31 * result + (approvedDateTime != null ? approvedDateTime.hashCode() : 0);
        result = 31 * result + (rejectedDateTime != null ? rejectedDateTime.hashCode() : 0);
        result = 31 * result + (lastSfmsSyncDateTime != null ? lastSfmsSyncDateTime.hashCode() : 0);
        result = 31 * result + (savedInSfms ? 1 : 0);
        return result;
    }

    public static class Builder {
        private int requisitionId;
        private int revisionId;
        private Employee customer;
        private Location destination;
        private Set<LineItem> lineItems;
        private RequisitionState state;
        private RequisitionStatus status;
        private Employee issuer;
        private String note;
        private String specialInstructions;
        private Employee modifiedBy;
        private LocalDateTime modifiedDateTime;
        private LocalDateTime orderedDateTime;
        private LocalDateTime processedDateTime;
        private LocalDateTime completedDateTime;
        private LocalDateTime approvedDateTime;
        private LocalDateTime rejectedDateTime;
        private LocalDateTime lastSfmsSyncDateTime;
        private boolean savedInSfms;

        public Builder withRequisitionId(int requisitionId) {
            this.requisitionId = requisitionId;
            return this;
        }

        public Builder withRevisionId(int revisionId) {
            this.revisionId = revisionId;
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

        public Builder withState(RequisitionState state) {
            this.state = state;
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

        public Builder withNote(String note) {
            this.note = note;
            return this;
        }

        public Builder withSpecialInstructions(String specialInstructions) {
            this.specialInstructions = specialInstructions;
            return this;
        }

        public Builder withModifiedBy(Employee modifiedBy) {
            this.modifiedBy = modifiedBy;
            return this;
        }

        public Builder withModifiedDateTime(LocalDateTime modifiedDateTime) {
            this.modifiedDateTime = modifiedDateTime;
            return this;
        }

        public Builder withOrderedDateTime(LocalDateTime orderedDateTime) {
            this.orderedDateTime = orderedDateTime;
            return this;
        }

        public Builder withProcessedDateTime(LocalDateTime processedDateTime) {
            this.processedDateTime = processedDateTime;
            return this;
        }

        public Builder withCompletedDateTime(LocalDateTime completedDateTime) {
            this.completedDateTime = completedDateTime;
            return this;
        }

        public Builder withApprovedDateTime(LocalDateTime approvedDateTime) {
            this.approvedDateTime = approvedDateTime;
            return this;
        }

        public Builder withLastSfmsSyncDateTimeDateTime(LocalDateTime lastSfmsSyncDateTime) {
            this.lastSfmsSyncDateTime = lastSfmsSyncDateTime;
            return this;
        }

        public Builder withRejectedDateTime(LocalDateTime rejectedDateTime) {
            this.rejectedDateTime = rejectedDateTime;
            return this;
        }

        public Builder withSavedInSfms(boolean savedInSfms) {
            this.savedInSfms = savedInSfms;
            return this;
        }

        public Requisition build() {
            return new Requisition(this);
        }
    }
}
