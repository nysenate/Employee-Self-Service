package gov.nysenate.ess.supply.shipment.dao;

import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.shipment.Shipment;
import gov.nysenate.ess.supply.shipment.ShipmentHistory;
import gov.nysenate.ess.supply.shipment.ShipmentVersion;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class InMemoryShipmentDao implements ShipmentDao {

    private Map<Integer, Shipment> shipments;

    public InMemoryShipmentDao() {
        this.shipments = new HashMap<>();
    }

    @Override
    public int insert(Order order, ShipmentVersion version, LocalDateTime modifiedDateTime) {
        ShipmentHistory history = ShipmentHistory.of(modifiedDateTime, version);
        Shipment shipment = Shipment.of(shipments.size() + 1, order, history);
        shipments.put(shipment.getId(), shipment);
        return shipment.getId();
    }

    @Override
    public void save(Shipment processed) {
        shipments.put(processed.getId(), processed);
    }

    @Override
    public Shipment getById(int shipmentId) {
        return shipments.get(shipmentId);
    }
}
