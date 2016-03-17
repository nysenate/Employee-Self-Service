package gov.nysenate.ess.supply.shipment.dao;

import java.time.LocalDateTime;

public interface ShipmentHistoryDao {

    void insertHistory(int shipmentId, int versionId, LocalDateTime modifiedDateTime);
}
