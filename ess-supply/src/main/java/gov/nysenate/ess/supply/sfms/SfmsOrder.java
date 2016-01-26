package gov.nysenate.ess.supply.sfms;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the order structure from the Sfms database.
 * Should only be used to verify Sfms data.
 */
public class SfmsOrder {

    private final SfmsOrderId orderId;
    private String fromLocationCode;
    private String fromLocationType;
    private LocalDateTime updateDateTime;
    private LocalDateTime originDateTime;
    private String updateEmpUid;
    private String originalEmpUid;
    private String issuedBy;
    private String responsibilityCenterHead;
    private Set<SfmsLineItem> items = new HashSet<>();

    public SfmsOrder(SfmsOrderId orderId) {
        this.orderId = orderId;
    }

    public SfmsOrderId getOrderId() {
        return orderId;
    }

    public String getFromLocationCode() {
        return fromLocationCode;
    }

    public void setFromLocationCode(String fromLocationCode) {
        this.fromLocationCode = fromLocationCode;
    }

    public String getFromLocationType() {
        return fromLocationType;
    }

    public void setFromLocationType(String fromLocationType) {
        this.fromLocationType = fromLocationType;
    }

    public LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(LocalDateTime updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public LocalDateTime getOriginDateTime() {
        return originDateTime;
    }

    public void setOriginDateTime(LocalDateTime originDateTime) {
        this.originDateTime = originDateTime;
    }

    public String getUpdateEmpUid() {
        return updateEmpUid;
    }

    public void setUpdateEmpUid(String updateEmpUid) {
        this.updateEmpUid = updateEmpUid;
    }

    public String getOriginalEmpUid() {
        return originalEmpUid;
    }

    public void setOriginalEmpUid(String originalEmpUid) {
        this.originalEmpUid = originalEmpUid;
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    public String getResponsibilityCenterHead() {
        return responsibilityCenterHead;
    }

    public void setResponsibilityCenterHead(String responsibilityCenterHead) {
        this.responsibilityCenterHead = responsibilityCenterHead;
    }

    public Set<SfmsLineItem> getItems() {
        return items;
    }

    public void setItems(Set<SfmsLineItem> items) {
        this.items = items;
    }

    public void addItem(SfmsLineItem item) {
        this.items.add(item);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SfmsOrder sfmsOrder = (SfmsOrder) o;

        if (orderId != null ? !orderId.equals(sfmsOrder.orderId) : sfmsOrder.orderId != null) return false;
        if (fromLocationCode != null ? !fromLocationCode.equals(sfmsOrder.fromLocationCode) : sfmsOrder.fromLocationCode != null)
            return false;
        if (fromLocationType != null ? !fromLocationType.equals(sfmsOrder.fromLocationType) : sfmsOrder.fromLocationType != null)
            return false;
        if (updateDateTime != null ? !updateDateTime.equals(sfmsOrder.updateDateTime) : sfmsOrder.updateDateTime != null)
            return false;
        if (originDateTime != null ? !originDateTime.equals(sfmsOrder.originDateTime) : sfmsOrder.originDateTime != null)
            return false;
        if (updateEmpUid != null ? !updateEmpUid.equals(sfmsOrder.updateEmpUid) : sfmsOrder.updateEmpUid != null)
            return false;
        if (originalEmpUid != null ? !originalEmpUid.equals(sfmsOrder.originalEmpUid) : sfmsOrder.originalEmpUid != null)
            return false;
        if (issuedBy != null ? !issuedBy.equals(sfmsOrder.issuedBy) : sfmsOrder.issuedBy != null) return false;
        if (responsibilityCenterHead != null ? !responsibilityCenterHead.equals(sfmsOrder.responsibilityCenterHead) : sfmsOrder.responsibilityCenterHead != null)
            return false;
        return !(items != null ? !items.equals(sfmsOrder.items) : sfmsOrder.items != null);
    }

    @Override
    public int hashCode() {
        int result = orderId != null ? orderId.hashCode() : 0;
        result = 31 * result + (fromLocationCode != null ? fromLocationCode.hashCode() : 0);
        result = 31 * result + (fromLocationType != null ? fromLocationType.hashCode() : 0);
        result = 31 * result + (updateDateTime != null ? updateDateTime.hashCode() : 0);
        result = 31 * result + (originDateTime != null ? originDateTime.hashCode() : 0);
        result = 31 * result + (updateEmpUid != null ? updateEmpUid.hashCode() : 0);
        result = 31 * result + (originalEmpUid != null ? originalEmpUid.hashCode() : 0);
        result = 31 * result + (issuedBy != null ? issuedBy.hashCode() : 0);
        result = 31 * result + (responsibilityCenterHead != null ? responsibilityCenterHead.hashCode() : 0);
        result = 31 * result + (items != null ? items.hashCode() : 0);
        return result;
    }
}
