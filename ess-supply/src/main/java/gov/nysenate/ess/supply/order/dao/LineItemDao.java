package gov.nysenate.ess.supply.order.dao;

import gov.nysenate.ess.supply.order.OrderVersion;

public interface LineItemDao {

    void insertVersionLineItems(OrderVersion version, int versionId);
}
