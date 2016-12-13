package gov.nysenate.ess.supply.item.dao;

import gov.nysenate.ess.supply.item.model.Category;
import gov.nysenate.ess.supply.item.model.SupplyItem;

import java.util.*;

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
//        items.put(1, new SupplyItem(1, "P2", "Number 2 Yellow Pencils", "24/PKG", new Category("Pencils"), 1, 1, 24, ItemVisibility.VISIBLE, true));
//        items.put(2, new SupplyItem(2, "PBL", "Blue ink, bold point", "DOZEN", new Category("Pens"), 1,1, 12, ItemVisibility.VISIBLE, true));
//        items.put(3, new SupplyItem(3, "PRE", "Red ink, medium point", "DOZEN", new Category("Pens"), 1,1, 12, ItemVisibility.VISIBLE, true));
//        items.put(4, new SupplyItem(4, "BL", "2\" binder clip", "DOZEN", new Category("Clips"), 1, 3, 12, ItemVisibility.VISIBLE, true));
//        items.put(5, new SupplyItem(5, "BS", "5/16\" binder clip", "DOZEN", new Category("Clips"), 1, 3, 12, ItemVisibility.VISIBLE, true));
//        items.put(6, new SupplyItem(6, "P5", "Large, silver, paper clips", "100/PKG", new Category("Clips"), 1, 10, 100, ItemVisibility.VISIBLE, true));
//        items.put(7, new SupplyItem(7, "PI3x3", "Regular size, 3x3 post-it notes", "DOZEN", new Category("Post-it Notes"), 1, 1, 12, ItemVisibility.VISIBLE, true));
//        items.put(7, new SupplyItem(7, "PI3x3", "Regular size, 3x3 post-it notes", "DOZEN", new Category("Post-it Notes"), 1, 1, 12, ItemVisibility.VISIBLE, true));
//        items.put(8, new SupplyItem(8, "PI3x5", "Large size, 3x5 post-it notes", "DOZEN", new Category("Post-it Notes"), 1, 1, 12, ItemVisibility.VISIBLE, true));
//        items.put(9, new SupplyItem(9, "PI1-1/2", "Mini size, 1.5x2 post-it notes", "DOZEN", new Category("Post-it Notes"), 1, 1, 12, ItemVisibility.VISIBLE, true));
    }

    @Override
    public Set<SupplyItem> getSupplyItems() {
        return new HashSet<>(items.values());
    }

    @Override
    public SupplyItem getItemById(Integer id) {
        return items.get(id);
    }
}
