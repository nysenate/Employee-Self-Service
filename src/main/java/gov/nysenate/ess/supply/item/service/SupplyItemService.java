package gov.nysenate.ess.supply.item.service;

import gov.nysenate.ess.supply.item.model.SupplyItem;

import java.util.Set;

public interface SupplyItemService {

    Set<SupplyItem> getSupplyItems();

    SupplyItem getItemById(Integer id);
}
