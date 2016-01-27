package gov.nysenate.ess.supply.sfms;

import gov.nysenate.ess.supply.order.Order;

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
}
