package gov.nysenate.ess.supply.shipment.dao;

import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.shipment.Shipment;
import gov.nysenate.ess.supply.shipment.ShipmentVersion;

import java.time.LocalDateTime;

public interface ShipmentDao {

    int insert(Order order, ShipmentVersion version, LocalDateTime modifiedDateTime);

    void save(Shipment processed);

    Shipment getById(int shipmentId);
}
