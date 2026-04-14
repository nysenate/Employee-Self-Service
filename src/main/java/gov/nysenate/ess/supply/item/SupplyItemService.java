package gov.nysenate.ess.supply.item;

import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.item.dao.SupplyItemDao;
import gov.nysenate.ess.supply.item.model.Category;
import gov.nysenate.ess.supply.item.model.SupplyItem;
import gov.nysenate.ess.supply.statistics.location.SupplyLocationStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SupplyItemService {

    private SupplyItemDao supplyItemDao;

    @Autowired
    public SupplyItemService(SupplyItemDao supplyItemDao) {
        this.supplyItemDao = supplyItemDao;
    }

    /**
     * Get all Supply Items.
     *
     * @return
     */
    public Set<SupplyItem> getAllItems() {
        return supplyItemDao.getSupplyItems();
    }

    /**
     * Get items that can be ordered from a given location.
     *
     * @param locId
     * @return
     */
    public Set<SupplyItem> getItems(LocationId locId) {
        Set<SupplyItem> items = getAllItems();
        return OrderableItems.forItemsAndLoc(items, locId);
    }

    /**
     * Filters the given list of items by the provided options.
     *
     * @param items
     * @param categoryNames
     * @param term
     * @param sort
     * @param limitOffset
     * @return
     */
    public PaginatedList<SupplyItem> filterItems(
            Set<SupplyItem> items,
            Set<String> categoryNames,
            String term,
            String sort,
            LimitOffset limitOffset) {
        items = ItemFilters.byCategories(items, categoryNames);
        items = ItemFilters.byTerm(items, term);
        List<SupplyItem> sortedItems = ItemFilters.bySort(items, sort);
        return ItemFilters.byLimitOffset(sortedItems, limitOffset);
    }
}
