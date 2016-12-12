package gov.nysenate.ess.supply.item.service;

import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.item.Category;
import gov.nysenate.ess.supply.item.SupplyItem;

import java.util.List;
import java.util.Set;

public interface SupplyItemService {

    Set<SupplyItem> getSupplyItems();

    SupplyItem getItemById(Integer id);
}
