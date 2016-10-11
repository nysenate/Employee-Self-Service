package gov.nysenate.ess.supply.requisition.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.client.view.LocationView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.item.view.LineItemView;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@XmlRootElement
public class RequisitionView implements ViewObject {

    protected int requisitionId;
    protected int revisionId;
    protected EmployeeView customer;
    protected LocationView destination;
    protected Set<LineItemView> lineItems;
    protected String specialInstructions;
    protected String status;
    protected EmployeeView issuer;
    protected String note;
    protected EmployeeView modifiedBy;
    protected LocalDateTime modifiedDateTime;
    protected LocalDateTime orderedDateTime;
    protected LocalDateTime processedDateTime;
    protected LocalDateTime completedDateTime;
    protected LocalDateTime approvedDateTime;
    protected LocalDateTime rejectedDateTime;
    protected LocalDateTime lastSfmsSyncDateTime;

    protected boolean savedInSfms;

    public RequisitionView() {}

    public RequisitionView(Requisition requisition) {
        this.requisitionId = requisition.getRequisitionId();
        this.revisionId = requisition.getRevisionId();
        this.customer = new EmployeeView(requisition.getCustomer());
        this.destination = new LocationView(requisition.getDestination());
        this.lineItems = requisition.getLineItems().stream()
                                    .map(LineItemView::new)
                                    .collect(Collectors.toSet());
        this.specialInstructions = requisition.getSpecialInstructions().orElse(null);
        this.status = requisition.getStatus().toString();
        this.issuer = requisition.getIssuer().map(EmployeeView::new).orElse(null);
        this.note = requisition.getNote().orElse(null);
        this.modifiedBy = new EmployeeView(requisition.getModifiedBy());
        this.modifiedDateTime = requisition.getModifiedDateTime().orElse(null);
        this.orderedDateTime = requisition.getOrderedDateTime();
        this.processedDateTime = requisition.getProcessedDateTime().orElse(null);
        this.completedDateTime = requisition.getCompletedDateTime().orElse(null);
        this.approvedDateTime = requisition.getApprovedDateTime().orElse(null);
        this.rejectedDateTime = requisition.getRejectedDateTime().orElse(null);
        this.lastSfmsSyncDateTime = requisition.getLastSfmsSyncDateTime().orElse(null);
        this.savedInSfms = requisition.getSavedInSfms();
    }

    @JsonIgnore
    public Requisition toRequisition() {
        return new Requisition.Builder()
                .withRequisitionId(requisitionId)
                .withRevisionId(revisionId)
                .withCustomer(customer.toEmployee())
                .withDestination(destination.toLocation())
                .withLineItems(lineItems.stream().map(LineItemView::toLineItem).collect(Collectors.toSet()))
                .withSpecialInstructions(specialInstructions)
                .withStatus(RequisitionStatus.valueOf(status))
                .withIssuer(issuer == null ? null : issuer.toEmployee())
                .withNote(note)
                .withModifiedBy(modifiedBy.toEmployee())
                .withModifiedDateTime(modifiedDateTime)
                .withOrderedDateTime(orderedDateTime)
                .withProcessedDateTime(processedDateTime)
                .withCompletedDateTime(completedDateTime)
                .withApprovedDateTime(approvedDateTime)
                .withRejectedDateTime(rejectedDateTime)
                .withLastSfmsSyncDateTimeDateTime(lastSfmsSyncDateTime)
                .withSavedInSfms(savedInSfms)
                .build();
    }

    @XmlElement
    public int getRequisitionId() {
        return requisitionId;
    }

    @XmlElement
    public int getRevisionId() {
        return revisionId;
    }

    @XmlElement
    public EmployeeView getCustomer() {
        return customer;
    }

    @XmlElement
    public LocationView getDestination() {
        return destination;
    }

    @XmlElement
    public Set<LineItemView> getLineItems() {
        return lineItems;
    }

    @XmlElement
    public String getSpecialInstructions() {
        return specialInstructions;
    }

    @XmlElement
    public String getStatus() {
        return status;
    }

    @XmlElement
    public EmployeeView getIssuer() {
        return issuer;
    }

    @XmlElement
    public String getNote() {
        return note;
    }

    @XmlElement
    public EmployeeView getModifiedBy() {
        return modifiedBy;
    }

    @XmlElement
    public LocalDateTime getModifiedDateTime() {
        return modifiedDateTime;
    }

    @XmlElement
    public LocalDateTime getOrderedDateTime() {
        return orderedDateTime;
    }

    @XmlElement
    public LocalDateTime getProcessedDateTime() {
        return processedDateTime;
    }

    @XmlElement
    public LocalDateTime getCompletedDateTime() {
        return completedDateTime;
    }

    @XmlElement
    public LocalDateTime getApprovedDateTime() {
        return approvedDateTime;
    }

    @XmlElement
    public LocalDateTime getRejectedDateTime() {
        return rejectedDateTime;
    }

    @XmlElement
    public LocalDateTime getLastSfmsSyncDateTime() {
        return lastSfmsSyncDateTime;
    }

    @XmlElement
    public boolean isSavedInSfms() {
        return savedInSfms;
    }

    @Override
    public String getViewType() {
        return "requisition";
    }
}
