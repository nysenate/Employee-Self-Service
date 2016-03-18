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

    public ShipmentView() { }

    public ShipmentView(Shipment shipment) {
        this.id = shipment.getId();
        this.order = new OrderView(shipment.getOrder());
        this.activeVersion = new ShipmentVersionView(shipment.current());
        Map<LocalDateTime, ShipmentVersionView> historyViewMap = new TreeMap<>();
        shipment.getHistory().getHistory().forEach((d, v) -> historyViewMap.put(d, new ShipmentVersionView(v)));
        this.history = MapView.of(historyViewMap);
    }

    public Shipment toShipment() {
        SortedMap<LocalDateTime, ShipmentVersion> historyMap = new TreeMap<>();
        this.history.items.forEach((d, v) -> historyMap.put(d, v.toShipmentVersion()));
        return Shipment.of(id, order.toOrder(), ShipmentHistory.of(historyMap));
    }

    @Override
    public String getViewType() {
        return "shipment-view";
    }
}
