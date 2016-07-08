package gov.nysenate.ess.supply.requisition.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.client.view.LocationView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.item.view.LineItemView;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;

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
                .withStatus(RequisitionStatus.valueOf(status))
                .withIssuer(issuer.toEmployee())
                .withNote(note)
                .withModifiedBy(modifiedBy.toEmployee())
                .withModifiedDateTime(modifiedDateTime)
                .withOrderedDateTime(orderedDateTime)
                .withProcessedDateTime(processedDateTime)
                .withCompletedDateTime(completedDateTime)
                .withApprovedDateTime(approvedDateTime)
                .withRejectedDateTime(rejectedDateTime)
                .withSavedInSfms(savedInSfms)
                .build();
    }

    public int getRequisitionId() {
        return requisitionId;
    }

    public void setRequisitionId(int requisitionId) {
        this.requisitionId = requisitionId;
    }

    public int getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(int revisionId) {
        this.revisionId = revisionId;
    }

    public EmployeeView getCustomer() {
        return customer;
    }

    public void setCustomer(EmployeeView customer) {
        this.customer = customer;
    }

    public LocationView getDestination() {
        return destination;
    }

    public void setDestination(LocationView destination) {
        this.destination = destination;
    }

    public Set<LineItemView> getLineItems() {
        return lineItems;
    }

    public void setLineItems(Set<LineItemView> lineItems) {
        this.lineItems = lineItems;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public EmployeeView getIssuer() {
        return issuer;
    }

    public void setIssuer(EmployeeView issuer) {
        this.issuer = issuer;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public EmployeeView getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(EmployeeView modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public LocalDateTime getModifiedDateTime() {
        return modifiedDateTime;
    }

    public void setModifiedDateTime(LocalDateTime modifiedDateTime) {
        this.modifiedDateTime = modifiedDateTime;
    }

    public LocalDateTime getOrderedDateTime() {
        return orderedDateTime;
    }

    public void setOrderedDateTime(LocalDateTime orderedDateTime) {
        this.orderedDateTime = orderedDateTime;
    }

    public LocalDateTime getProcessedDateTime() {
        return processedDateTime;
    }

    public void setProcessedDateTime(LocalDateTime processedDateTime) {
        this.processedDateTime = processedDateTime;
    }

    public LocalDateTime getCompletedDateTime() {
        return completedDateTime;
    }

    public void setCompletedDateTime(LocalDateTime completedDateTime) {
        this.completedDateTime = completedDateTime;
    }

    public LocalDateTime getApprovedDateTime() {
        return approvedDateTime;
    }

    public void setApprovedDateTime(LocalDateTime approvedDateTime) {
        this.approvedDateTime = approvedDateTime;
    }

    public LocalDateTime getRejectedDateTime() {
        return rejectedDateTime;
    }

    public void setRejectedDateTime(LocalDateTime rejectedDateTime) {
        this.rejectedDateTime = rejectedDateTime;
    }

    public boolean isSavedInSfms() {
        return savedInSfms;
    }

    public void setSavedInSfms(boolean savedInSfms) {
        this.savedInSfms = savedInSfms;
    }

    @Override
    public String getViewType() {
        return "requisition";
    }
}
