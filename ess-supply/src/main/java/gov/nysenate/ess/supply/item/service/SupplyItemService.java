package gov.nysenate.ess.supply.item.service;

import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.item.Category;
import gov.nysenate.ess.supply.item.SupplyItem;

import java.util.List;

public interface SupplyItemService {

    PaginatedList<SupplyItem> getSupplyItems(LimitOffset limOff);

    /**
     * Search for supply items by categories.
     * @return A PaginatedList containing all SupplyItems belonging to any category given in <code>categories</code>.
     */
    PaginatedList<SupplyItem> getSupplyItemsByCategorys(List<Category> categories, LimitOffset limOff);

    SupplyItem getItemById(Integer id);
}
