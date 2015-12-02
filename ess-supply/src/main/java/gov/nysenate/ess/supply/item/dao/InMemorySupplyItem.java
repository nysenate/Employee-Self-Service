package gov.nysenate.ess.supply.item.dao;

import gov.nysenate.ess.supply.item.SupplyItem;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemorySupplyItem implements SupplyItemDao {

    private Map<String, SupplyItem> items;

    public InMemorySupplyItem() {
        items = new HashMap<>();
        items.put("P2", new SupplyItem("P2", "Pencils", "Number 2 Yellow Pencils",
                                       24, "Pencils", 2));

        items.put("PBL", new SupplyItem("PBL", "Blue Ballpoint Pens", "Blue ink, bold point",
                                       12, "Pens", 2));

    }

    @Override
    public List<SupplyItem> getSupplyItems() {
        return new ArrayList<>(items.values());
    }
}
