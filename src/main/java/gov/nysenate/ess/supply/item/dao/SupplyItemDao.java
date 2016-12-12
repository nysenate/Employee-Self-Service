package gov.nysenate.ess.supply.item.dao;

import gov.nysenate.ess.supply.item.model.SupplyItem;

import java.util.Set;

public interface SupplyItemDao {

    SupplyItem getItemById(Integer id);

    Set<SupplyItem> getSupplyItems();
}
