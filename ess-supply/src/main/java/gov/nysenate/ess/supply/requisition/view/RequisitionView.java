package gov.nysenate.ess.supply.requisition.view;
//
//import gov.nysenate.ess.core.client.view.base.ViewObject;
//import gov.nysenate.ess.supply.requisition.Requisition;
//
//import java.time.LocalDateTime;
//
///**
// * Simple view to represent a {@link Requisition} object without its history.
// * Use {@link DetailedRequisitionView} if you need the requisitions history.
// */
//public class RequisitionView implements ViewObject {
//
//    protected int id;
//    protected RequisitionVersionView activeVersion;
//    protected LocalDateTime orderedDateTime;
//    protected LocalDateTime processedDateTime;
//    protected LocalDateTime completedDateTime;
//    protected LocalDateTime approvedDateTime;
//    protected LocalDateTime rejectedDateTime;
//    protected LocalDateTime modifiedDateTime;
//
//    public RequisitionView() {}
//
//    public RequisitionView(Requisition requisition) {
//        this.id = requisition.getId();
//        this.activeVersion = new RequisitionVersionView(requisition.getCurrentVersion());
//        this.orderedDateTime = requisition.getOrderedDateTime();
//        this.processedDateTime = requisition.getProcessedDateTime().orElse(null);
//        this.completedDateTime = requisition.getCompletedDateTime().orElse(null);
//        this.approvedDateTime = requisition.getApprovedDateTime().orElse(null);
//        this.rejectedDateTime = requisition.getRejectedDateTime().orElse(null);
//        this.modifiedDateTime = requisition.getModifiedDateTime();
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public RequisitionVersionView getActiveVersion() {
//        return activeVersion;
//    }
//
//    public void setActiveVersion(RequisitionVersionView activeVersion) {
//        this.activeVersion = activeVersion;
//    }
//
//    public LocalDateTime getOrderedDateTime() {
//        return orderedDateTime;
//    }
//
//    public void setOrderedDateTime(LocalDateTime orderedDateTime) {
//        this.orderedDateTime = orderedDateTime;
//    }
//
//    public LocalDateTime getProcessedDateTime() {
//        return processedDateTime;
//    }
//
//    public void setProcessedDateTime(LocalDateTime processedDateTime) {
//        this.processedDateTime = processedDateTime;
//    }
//
//    public LocalDateTime getCompletedDateTime() {
//        return completedDateTime;
//    }
//
//    public void setCompletedDateTime(LocalDateTime completedDateTime) {
//        this.completedDateTime = completedDateTime;
//    }
//
//    public LocalDateTime getApprovedDateTime() {
//        return approvedDateTime;
//    }
//
//    public void setApprovedDateTime(LocalDateTime approvedDateTime) {
//        this.approvedDateTime = approvedDateTime;
//    }
//
//    public LocalDateTime getRejectedDateTime() {
//        return rejectedDateTime;
//    }
//
//    public void setRejectedDateTime(LocalDateTime rejectedDateTime) {
//        this.rejectedDateTime = rejectedDateTime;
//    }
//
//    public LocalDateTime getModifiedDateTime() {
//        return modifiedDateTime;
//    }
//
//    public void setModifiedDateTime(LocalDateTime modifiedDateTime) {
//        this.modifiedDateTime = modifiedDateTime;
//    }
//
//    @Override
//    public String getViewType() {
//        return "requisition";
//    }
//}
