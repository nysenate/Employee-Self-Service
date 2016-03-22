package gov.nysenate.ess.supply.shipment.view;

import gov.nysenate.ess.core.client.view.base.MapView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.order.view.OrderView;
import gov.nysenate.ess.supply.shipment.Shipment;
import gov.nysenate.ess.supply.shipment.ShipmentHistory;
import gov.nysenate.ess.supply.shipment.ShipmentVersion;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class ShipmentView implements ViewObject {

    protected int id;
    protected OrderView order;
    protected ShipmentVersionView activeVersion;
    protected MapView<LocalDateTime, ShipmentVersionView> history;
    protected LocalDateTime processedDateTime;
    protected LocalDateTime completedDateTime;
    protected LocalDateTime approvedDateTime;
    protected LocalDateTime canceledDateTime;

    public ShipmentView() { }

    public ShipmentView(Shipment shipment) {
        this.id = shipment.getId();
        this.order = new OrderView(shipment.getOrder());
        this.activeVersion = new ShipmentVersionView(shipment.current());
        Map<LocalDateTime, ShipmentVersionView> historyViewMap = new TreeMap<>();
        shipment.getHistory().getHistory().forEach((d, v) -> historyViewMap.put(d, new ShipmentVersionView(v)));
        this.history = MapView.of(historyViewMap);
        this.processedDateTime = shipment.getProcessedDateTime().orElse(null);
        this.completedDateTime = shipment.getCompletedDateTime().orElse(null);
        this.approvedDateTime = shipment.getApprovedDateTime().orElse(null);
        this.canceledDateTime = shipment.getCanceledDateTime().orElse(null);
    }

    public Shipment toShipment() {
        SortedMap<LocalDateTime, ShipmentVersion> historyMap = new TreeMap<>();
        this.history.items.forEach((d, v) -> historyMap.put(d, v.toShipmentVersion()));
        return Shipment.of(id, order.toOrder(), ShipmentHistory.of(historyMap));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public OrderView getOrder() {
        return order;
    }

    public void setOrder(OrderView order) {
        this.order = order;
    }

    public ShipmentVersionView getActiveVersion() {
        return activeVersion;
    }

    public void setActiveVersion(ShipmentVersionView activeVersion) {
        this.activeVersion = activeVersion;
    }

    public MapView<LocalDateTime, ShipmentVersionView> getHistory() {
        return history;
    }

    public void setHistory(MapView<LocalDateTime, ShipmentVersionView> history) {
        this.history = history;
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

    public LocalDateTime getCanceledDateTime() {
        return canceledDateTime;
    }

    public void setCanceledDateTime(LocalDateTime canceledDateTime) {
        this.canceledDateTime = canceledDateTime;
    }

    @Override
    public String getViewType() {
        return "shipment-view";
    }
}
