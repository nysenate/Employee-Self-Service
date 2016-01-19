package gov.nysenate.ess.supply.item.dao;

import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.item.SupplyItem;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Primary
@Profile("test")
@Repository
public class InMemorySupplyItem implements SupplyItemDao {

    private Map<Integer, SupplyItem> items;

    public InMemorySupplyItem() {
        reset();
    }

    public void reset() {
        items = new HashMap<>();
        items.put(1, new SupplyItem(1, "P2", "Pencils", "Number 2 Yellow Pencils", "24/Pack", "Pencils", 1));
        items.put(2, new SupplyItem(2, "PBL", "Blue Ballpoint Pens", "Blue ink, bold point", "12/Pack", "Pens", 1));
        items.put(3, new SupplyItem(3, "PRE", "Red Ballpoint Pens", "Red ink, medium point", "12/Pack", "Pens", 1));
        items.put(4, new SupplyItem(4, "BL", "Large Binder Clip", "2\" binder clip", "12/Pack", "Clips", 3));
        items.put(5, new SupplyItem(5, "BS", "Small Binder Clip", "5/16\" binder clip", "12/Pack", "Clips", 3));
        items.put(6, new SupplyItem(6, "P5", "Large Paper Clip", "Large, silver, paper clips", "100/Pack", "Clips", 10));
        items.put(7, new SupplyItem(7, "PI3x3", "Post-it Notes, Regular", "Regular size, 3x3 post-it notes", "12/Pack", "Post-it Notes", 1));
        items.put(7, new SupplyItem(7, "PI3x3", "Regular Post-it Notes", "Regular size, 3x3 post-it notes", "12/Pack", "Post-it Notes", 1));
        items.put(8, new SupplyItem(8, "PI3x5", "Large Post-it Notes", "Large size, 3x5 post-it notes", "12/Pack", "Post-it Notes", 1));
        items.put(9, new SupplyItem(9, "PI1-1/2", "Mini Post-it Notes", "Mini size, 1.5x2 post-it notes", "12/Pack", "Post-it Notes", 1));
    }

    @Override
    public List<SupplyItem> getSupplyItems(LimitOffset limOff) {
        return new ArrayList<>(items.values());
    }

    @Override
    public SupplyItem getItemById(Integer id) {
        return items.get(id);
    }
}
