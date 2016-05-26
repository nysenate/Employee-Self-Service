package gov.nysenate.ess.supply.requisition.view;

import gov.nysenate.ess.core.client.view.base.MapView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionVersion;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class RequisitionView implements ViewObject {

    protected int id;
    protected RequisitionVersionView activeVersion;
    protected MapView<LocalDateTime, RequisitionVersionView> history;
    protected LocalDateTime orderedDateTime;
    protected LocalDateTime processedDateTime;
    protected LocalDateTime completedDateTime;
    protected LocalDateTime approvedDateTime;
    protected LocalDateTime rejectedDateTime;
    protected LocalDateTime modifiedDateTime;

    public RequisitionView() {}

    public RequisitionView(Requisition requisition) {
        this.id = requisition.getId();
        this.activeVersion = new RequisitionVersionView(requisition.getCurrentVersion());
        Map<LocalDateTime, RequisitionVersionView> historyMap = new TreeMap<>();
        for (Map.Entry<LocalDateTime, RequisitionVersion> entry: requisition.getHistory().entrySet()) {
            historyMap.put(entry.getKey(), new RequisitionVersionView(entry.getValue()));
        }
        this.history = MapView.of(historyMap);
        this.orderedDateTime = requisition.getOrderedDateTime();
        this.processedDateTime = requisition.getProcessedDateTime().orElse(null);
        this.completedDateTime = requisition.getCompletedDateTime().orElse(null);
        this.approvedDateTime = requisition.getApprovedDateTime().orElse(null);
        this.rejectedDateTime = requisition.getRejectedDateTime().orElse(null);
        this.modifiedDateTime = requisition.getModifiedDateTime();
    }

    public Requisition toRequisition() {
        SortedMap<LocalDateTime, RequisitionVersion> historyMap = new TreeMap<>();
        for (Map.Entry<LocalDateTime, RequisitionVersionView> entry: this.history.items.entrySet()) {
            historyMap.put(entry.getKey(), entry.getValue().toRequisitionVersion());
        }
        Requisition requisition = new Requisition(historyMap);
        requisition.setProcessedDateTime(processedDateTime);
        requisition.setCompletedDateTime(completedDateTime);
        requisition.setApprovedDateTime(approvedDateTime);
        requisition.setRejectedDateTime(rejectedDateTime);
        return requisition;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public RequisitionVersionView getActiveVersion() {
        return activeVersion;
    }

    public void setActiveVersion(RequisitionVersionView activeVersion) {
        this.activeVersion = activeVersion;
    }

    public MapView<LocalDateTime, RequisitionVersionView> getHistory() {
        return history;
    }

    public void setHistory(MapView<LocalDateTime, RequisitionVersionView> history) {
        this.history = history;
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

    public LocalDateTime getModifiedDateTime() {
        return modifiedDateTime;
    }

    public void setModifiedDateTime(LocalDateTime modifiedDateTime) {
        this.modifiedDateTime = modifiedDateTime;
    }

    @Override
    public String getViewType() {
        return "requisition";
    }
}
