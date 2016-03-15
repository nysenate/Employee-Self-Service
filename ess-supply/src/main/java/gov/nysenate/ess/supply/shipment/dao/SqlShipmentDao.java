package gov.nysenate.ess.supply.shipment.dao;

import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.shipment.Shipment;
import gov.nysenate.ess.supply.shipment.ShipmentVersion;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class SqlShipmentDao implements ShipmentDao{

    @Override
    public int insert(Order order, ShipmentVersion version, LocalDateTime modifiedDateTime) {
        return 0;
    }

    @Override
    public void save(Shipment processed) {

    }

    @Override
    public Shipment getById(int shipmentId) {
        return null;
    }
}
