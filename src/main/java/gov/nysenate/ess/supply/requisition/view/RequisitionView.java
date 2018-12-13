package gov.nysenate.ess.supply.requisition.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.client.view.LocationView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.item.view.LineItemView;
import gov.nysenate.ess.supply.requisition.model.DeliveryMethod;
import gov.nysenate.ess.supply.requisition.model.Requisition;
import gov.nysenate.ess.supply.requisition.model.RequisitionState;
import gov.nysenate.ess.supply.requisition.model.RequisitionStatus;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A view representing a Requisition.
 */
@XmlRootElement
public class RequisitionView implements ViewObject {

    protected int requisitionId;
    protected int revisionId;
    protected EmployeeView customer;
    protected LocationView destination;
    protected String deliveryMethod;
    protected Set<LineItemView> lineItems;
    protected String specialInstructions;
    protected String status;
    protected EmployeeView issuer;
    protected String note;
    protected EmployeeView modifiedBy;
    protected String modifiedDateTime;
    protected String orderedDateTime;
    protected String processedDateTime;
    protected String completedDateTime;
    protected String approvedDateTime;
    protected String rejectedDateTime;
    protected String lastSfmsSyncDateTime;

    protected boolean savedInSfms;

    public RequisitionView() {}

    public RequisitionView(Requisition requisition) {
        this.requisitionId = requisition.getRequisitionId();
        this.revisionId = requisition.getRevisionId();
        this.customer = new EmployeeView(requisition.getCustomer());
        this.destination = new LocationView(requisition.getDestination());
        this.deliveryMethod = requisition.getDeliveryMethod().name();
        this.lineItems = requisition.getLineItems().stream()
                                    .map(LineItemView::new)
                                    .collect(Collectors.toSet());
        this.specialInstructions = requisition.getSpecialInstructions().orElse(null);
        this.status = requisition.getStatus().toString();
        this.issuer = requisition.getIssuer().map(EmployeeView::new).orElse(null);
        this.note = requisition.getNote().orElse(null);
        this.modifiedBy = new EmployeeView(requisition.getModifiedBy());
        this.modifiedDateTime = dateTimeToString(requisition.getModifiedDateTime());
        this.orderedDateTime = requisition.getOrderedDateTime() == null ? null : requisition.getOrderedDateTime().format(DateTimeFormatter.ISO_DATE_TIME);
        this.processedDateTime = dateTimeToString(requisition.getProcessedDateTime());
        this.completedDateTime = dateTimeToString(requisition.getCompletedDateTime());
        this.approvedDateTime = dateTimeToString(requisition.getApprovedDateTime());
        this.rejectedDateTime = dateTimeToString(requisition.getRejectedDateTime());
        this.lastSfmsSyncDateTime = dateTimeToString(requisition.getLastSfmsSyncDateTime());
        this.savedInSfms = requisition.getSavedInSfms();
    }

    @JsonIgnore
    public Requisition toRequisition() {
        return new Requisition.Builder()
                .withRequisitionId(requisitionId)
                .withRevisionId(revisionId)
                .withCustomer(customer.toEmployee())
                .withDestination(destination.toLocation())
                .withDeliveryMethod(DeliveryMethod.valueOf(deliveryMethod))
                .withLineItems(lineItems.stream().map(LineItemView::toLineItem).collect(Collectors.toSet()))
                .withState(RequisitionState.of(RequisitionStatus.valueOf(status)))
                .withSpecialInstructions(specialInstructions)
                .withIssuer(issuer == null ? null : issuer.toEmployee())
                .withNote(note)
                .withModifiedBy(modifiedBy.toEmployee())
                .withModifiedDateTime(stringToDateTime(modifiedDateTime))
                .withOrderedDateTime(stringToDateTime(orderedDateTime))
                .withProcessedDateTime(stringToDateTime(processedDateTime))
                .withCompletedDateTime(stringToDateTime(completedDateTime))
                .withApprovedDateTime(stringToDateTime(approvedDateTime))
                .withRejectedDateTime(stringToDateTime(rejectedDateTime))
                .withLastSfmsSyncDateTimeDateTime(stringToDateTime(lastSfmsSyncDateTime))
                .withSavedInSfms(savedInSfms)
                .build();
    }

    @JsonIgnore
    private String dateTimeToString(Optional<LocalDateTime> dtOption) {
        return dtOption.map(dt -> dt.format(DateTimeFormatter.ISO_DATE_TIME)).orElse(null);
    }

    @JsonIgnore
    private LocalDateTime stringToDateTime(String dt) {
        return dt == null ? null : LocalDateTime.parse(dt, DateTimeFormatter.ISO_DATE_TIME);
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
    public String getDeliveryMethod() {
        return deliveryMethod;
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
    public String getModifiedDateTime() {
        return modifiedDateTime;
    }

    @XmlElement
    public String getOrderedDateTime() {
        return orderedDateTime;
    }

    @XmlElement
    public String getProcessedDateTime() {
        return processedDateTime;
    }

    @XmlElement
    public String getCompletedDateTime() {
        return completedDateTime;
    }

    @XmlElement
    public String getApprovedDateTime() {
        return approvedDateTime;
    }

    @XmlElement
    public String getRejectedDateTime() {
        return rejectedDateTime;
    }

    @XmlElement
    public String getLastSfmsSyncDateTime() {
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
