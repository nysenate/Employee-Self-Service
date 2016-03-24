package gov.nysenate.ess.supply.item.dao;

import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.item.SupplyItem;

public interface SupplyItemDao {

    PaginatedList<SupplyItem> getSupplyItems(LimitOffset limOff);

    SupplyItem getItemById(Integer id);
}
