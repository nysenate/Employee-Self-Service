package gov.nysenate.ess.supply.shipment.dao;

import gov.nysenate.ess.supply.shipment.Shipment;

import java.util.HashMap;
import java.util.Map;

public class InMemoryShipmentDao implements ShipmentDao {

    private Map<Integer, Shipment> shipments;

    public InMemoryShipmentDao() {
        this.shipments = new HashMap<>();
    }
}
