package gov.nysenate.ess.supply.shipment.dao;

import gov.nysenate.ess.supply.shipment.ShipmentVersion;

public interface ShipmentVersionDao {

    int insertVersion(ShipmentVersion version);

    ShipmentVersion getVersionById(int versionId);
}
