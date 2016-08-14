package gov.nysenate.ess.supply.item.dao;

import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.item.Category;
import gov.nysenate.ess.supply.item.SupplyItem;

import java.util.List;

public interface SupplyItemDao {

    SupplyItem getItemById(Integer id);

    PaginatedList<SupplyItem> getSupplyItems(LimitOffset limOff);

    PaginatedList<SupplyItem> getSupplyItemsByCategories(List<Category> categories, LimitOffset limOff);
}
