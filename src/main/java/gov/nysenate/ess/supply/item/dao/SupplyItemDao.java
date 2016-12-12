package gov.nysenate.ess.supply.item.dao;

import gov.nysenate.ess.supply.item.SupplyItem;

import java.util.Set;

public interface SupplyItemDao {

    SupplyItem getItemById(Integer id);

    Set<SupplyItem> getSupplyItems();
}
