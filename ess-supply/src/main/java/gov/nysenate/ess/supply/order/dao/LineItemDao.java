package gov.nysenate.ess.supply.order.dao;

import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.order.OrderVersion;

import java.util.Set;

public interface LineItemDao {

    void insertVersionLineItems(OrderVersion version, int versionId);

    Set<LineItem> getLineItems(int versionId);
}
