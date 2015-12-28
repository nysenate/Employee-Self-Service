package gov.nysenate.ess.supply.item.dao;

import gov.nysenate.ess.supply.item.SupplyItem;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemorySupplyItem implements SupplyItemDao {

    private Map<Integer, SupplyItem> items;

    public InMemorySupplyItem() {
        reset();
    }

    public void reset() {
        items = new HashMap<>();
        items.put(1, new SupplyItem(1, "P2", "Pencils", "Number 2 Yellow Pencils", 24, "Pencils", 1));
        items.put(2, new SupplyItem(2, "PBL", "Blue Ballpoint Pens", "Blue ink, bold point", 12, "Pens", 1));
        items.put(3, new SupplyItem(3, "PRE", "Red Ballpoint Pens", "Red ink, medium point", 12, "Pens", 1));
        items.put(4, new SupplyItem(4, "BL", "Large Binder Clip", "2\" binder clip", 12, "Clips", 3));
        items.put(5, new SupplyItem(5, "BS", "Small Binder Clip", "5/16\" binder clip", 12, "Clips", 3));
        items.put(3, new SupplyItem(6, "P5", "Large Paper Clip", "Large, silver, paper clips", 100, "Clips", 10));
    }

    @Override
    public List<SupplyItem> getSupplyItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public SupplyItem getItemById(Integer id) {
        return items.get(id);
    }
}
