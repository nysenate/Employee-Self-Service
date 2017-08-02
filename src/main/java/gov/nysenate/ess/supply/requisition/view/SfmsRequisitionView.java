package gov.nysenate.ess.supply.requisition.view;

import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.requisition.model.Requisition;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A view of a requisition formatted specifically for the SFMS synchronization process.
 * Any updates to this view will need to be updated in the oracle-synchronization.sql script.
 */
@XmlRootElement(name = "Requisition")
@XmlAccessorType(XmlAccessType.FIELD)
public class SfmsRequisitionView {

    protected int requisitionId;
    protected int customerId;
    protected String destinationCode;
    protected char destinationTypeCode;
    protected Set<SfmsLineItemView> lineItems;
    protected String issuerUid;
    protected LocalDateTime approvedDateTime;

    public SfmsRequisitionView(Requisition requisition) {
        this.requisitionId = requisition.getRequisitionId();
        this.customerId = requisition.getCustomer().getEmployeeId();
        this.destinationCode = requisition.getDestination().getLocId().getCode();
        this.destinationTypeCode = requisition.getDestination().getLocId().getType().getCode();
        this.lineItems = requisition.getLineItems().stream()
                .map(SfmsLineItemView::new)
                .collect(Collectors.toSet());
        this.issuerUid = requisition.getIssuer().get().getUid();
        this.approvedDateTime = requisition.getApprovedDateTime().get();
    }

    public int getRequisitionId() {
        return requisitionId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getDestinationCode() {
        return destinationCode;
    }

    public char getDestinationTypeCode() {
        return destinationTypeCode;
    }

    public Set<SfmsLineItemView> getLineItems() {
        return lineItems;
    }

    public String getIssuerUid() {
        return issuerUid;
    }

    public LocalDateTime getApprovedDateTime() {
        return approvedDateTime;
    }

    /**
     * A view of LineItem formatted specifically for the synchronization process.
     */
    public static class SfmsLineItemView {
       protected int itemId;
       protected int quantity;
       protected String issueUnit;

       public SfmsLineItemView(LineItem li) {
           this.itemId = li.getItem().getId();
           this.quantity = li.getQuantity();
           this.issueUnit = li.getItem().getUnitDescription();
       }

        public int getItemId() {
            return itemId;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getIssueUnit() {
            return issueUnit;
        }
    }
}
