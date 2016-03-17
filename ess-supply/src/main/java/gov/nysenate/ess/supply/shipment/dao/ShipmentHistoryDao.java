package gov.nysenate.ess.supply.shipment.dao;

import gov.nysenate.ess.supply.shipment.ShipmentHistory;

import java.time.LocalDateTime;

public interface ShipmentHistoryDao {

    void insertHistory(int shipmentId, int versionId, LocalDateTime modifiedDateTime);

    ShipmentHistory getHistoryByShipmentId(int shipmentId);
}
