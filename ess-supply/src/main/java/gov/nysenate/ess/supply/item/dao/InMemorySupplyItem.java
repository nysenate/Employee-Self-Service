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
        items.put(1, new SupplyItem(1, "P2", "Pencils", "Number 2 Yellow Pencils",
                                       24, "Pencils", 2));
        items.put(2, new SupplyItem(2, "PBL", "Blue Ballpoint Pens", "Blue ink, bold point",
                                       12, "Pens", 2));
    }

    @Override
    public List<SupplyItem> getSupplyItems() {
        return new ArrayList<>(items.values());
    }
}
