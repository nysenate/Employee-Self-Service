package gov.nysenate.ess.supply.item.dao;

import gov.nysenate.ess.supply.item.SupplyItem;

import java.util.List;

public interface SupplyItemDao {

    List<SupplyItem> getSupplyItems();

    SupplyItem getItemById(Integer id);
}
