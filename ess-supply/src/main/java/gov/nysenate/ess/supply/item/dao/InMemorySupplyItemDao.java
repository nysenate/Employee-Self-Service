package gov.nysenate.ess.supply.item.dao;

import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.item.Category;
import gov.nysenate.ess.supply.item.SupplyItem;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used for testing across SFMS boundries.
 */
public class InMemorySupplyItemDao implements SupplyItemDao {

    private Map<Integer, SupplyItem> items;

    public InMemorySupplyItemDao() {
        reset();
    }

    public void reset() {
        items = new HashMap<>();
        items.put(1, new SupplyItem(1, "P2", "Number 2 Yellow Pencils", "24/PKG", new Category("Pencils"), 1,1, 24));
        items.put(2, new SupplyItem(2, "PBL", "Blue ink, bold point", "DOZEN", new Category("Pens"), 1,1, 12));
        items.put(3, new SupplyItem(3, "PRE", "Red ink, medium point", "DOZEN", new Category("Pens"), 1,1, 12));
        items.put(4, new SupplyItem(4, "BL", "2\" binder clip", "DOZEN", new Category("Clips"), 1, 3, 12));
        items.put(5, new SupplyItem(5, "BS", "5/16\" binder clip", "DOZEN", new Category("Clips"), 1, 3, 12));
        items.put(6, new SupplyItem(6, "P5", "Large, silver, paper clips", "100/PKG", new Category("Clips"), 1, 10, 100));
        items.put(7, new SupplyItem(7, "PI3x3", "Regular size, 3x3 post-it notes", "DOZEN", new Category("Post-it Notes"), 1, 1, 12));
        items.put(7, new SupplyItem(7, "PI3x3", "Regular size, 3x3 post-it notes", "DOZEN", new Category("Post-it Notes"), 1, 1, 12));
        items.put(8, new SupplyItem(8, "PI3x5", "Large size, 3x5 post-it notes", "DOZEN", new Category("Post-it Notes"), 1, 1, 12));
        items.put(9, new SupplyItem(9, "PI1-1/2", "Mini size, 1.5x2 post-it notes", "DOZEN", new Category("Post-it Notes"), 1, 1, 12));
    }

    @Override
    public PaginatedList<SupplyItem> getSupplyItems(LimitOffset limOff) {
        return new PaginatedList<SupplyItem>(items.size(), limOff, (ArrayList)items.values());
    }

    @Override
    public PaginatedList<SupplyItem> getSupplyItemsByCategories(List<Category> categories, LimitOffset limOff) {
        List<SupplyItem> matchingItems = new ArrayList<>();
        this.items.values().forEach(item -> {
            if (categories.contains(item.getCategory())) matchingItems.add(item);
        });
        return new PaginatedList<>(matchingItems.size(), limOff, matchingItems);
    }

    @Override
    public SupplyItem getItemById(Integer id) {
        return items.get(id);
    }
}
