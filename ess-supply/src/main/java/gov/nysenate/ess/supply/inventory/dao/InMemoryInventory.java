package gov.nysenate.ess.supply.inventory.dao;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.TreeMap;

@Repository
public class InMemoryInventory implements InventoryDao {

    private Map<Integer, Integer> itemIdToQuantitiesMap;

    public InMemoryInventory() {
        reset();
    }

    public void reset() {
        itemIdToQuantitiesMap = new TreeMap<>();
        itemIdToQuantitiesMap.put(1, 3);
        itemIdToQuantitiesMap.put(2, 8);
    }

    @Override
    public Map<Integer, Integer> getCurrentInventory() {
        return itemIdToQuantitiesMap;
    }
}
