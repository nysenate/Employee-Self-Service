package gov.nysenate.ess.supply.item.dao;

import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.item.SupplyItem;

import java.util.List;

public interface SupplyItemDao {

    List<SupplyItem> getSupplyItems(LimitOffset limOff);

    SupplyItem getItemById(Integer id);
}
