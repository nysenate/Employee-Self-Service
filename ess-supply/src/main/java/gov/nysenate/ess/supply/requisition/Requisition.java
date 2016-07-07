package gov.nysenate.ess.supply.requisition;

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
    private final RequisitionStatus status;
    private final Employee issuer;
    private final String note;
    private final Employee modifiedBy;
    private final LocalDateTime modifiedDateTime;
    private final LocalDateTime orderedDateTime;
    private final LocalDateTime processedDateTime;
    private final LocalDateTime completedDateTime;
    private final LocalDateTime approvedDateTime;
    private final LocalDateTime rejectedDateTime;
    private final boolean savedInSfms;

    private Requisition(Builder builder) {
        this.requisitionId = builder.requisitionId;
        this.revisionId = builder.revisionId;
        this.customer = checkNotNull(builder.customer, "Requisition requires non null customer.");
        this.destination = checkNotNull(builder.destination, "Requisition requires non null destination.");
        this.lineItems = ImmutableSet.copyOf(builder.lineItems);
        this.status = checkNotNull(builder.status, "Requisition requires non null status.");
        this.issuer = builder.issuer;
        this.note = builder.note;
        this.modifiedBy = checkNotNull(builder.modifiedBy, "Requisition requires a modified by employee.");
        this.modifiedDateTime = checkNotNull(builder.modifiedDateTime, "Requisition requires a modified date time.");
        this.orderedDateTime = checkNotNull(builder.orderedDateTime, "Requisition requires a ordered date time.");
        this.processedDateTime = builder.processedDateTime;
        this.completedDateTime = builder.completedDateTime;
        this.approvedDateTime = builder.approvedDateTime;
        this.rejectedDateTime = builder.rejectedDateTime;
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
                .withSavedInSfms(this.savedInSfms);
    }

    /** Basic Setters **/

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

    public Requisition setSavedInSfms(boolean savedInSfms) {
        return copy().withSavedInSfms(savedInSfms).build();
    }

    /** Basic Getters **/

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

    public LocalDateTime getModifiedDateTime() {
        return modifiedDateTime;
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

    public boolean getSavedInSfms() {
        return savedInSfms;
    }

    public static class Builder {
        private int requisitionId;
        private int revisionId;
        private Employee customer;
        private Location destination;
        private Set<LineItem> lineItems;
        private RequisitionStatus status;
        private Employee issuer;
        private String note;
        private Employee modifiedBy;
        private LocalDateTime modifiedDateTime;
        private LocalDateTime orderedDateTime;
        private LocalDateTime processedDateTime;
        private LocalDateTime completedDateTime;
        private LocalDateTime approvedDateTime;
        private LocalDateTime rejectedDateTime;
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
