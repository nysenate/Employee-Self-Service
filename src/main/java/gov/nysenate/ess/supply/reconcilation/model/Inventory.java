package gov.nysenate.ess.supply.reconcilation.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Inventory {

    private final Map<Integer, Integer> itemQuantities; // ItemId to quantity

    /**
     * Creates an empty inventory with the given itemIds.
     * @param itemIds
     */
    public Inventory(Collection<Integer> itemIds) {
        itemQuantities = new HashMap<>();
        for (int i : itemIds) {
            itemQuantities.put(i, null);
        }
    }

    public Inventory(Map<Integer, Integer> itemQuantities) {
        this.itemQuantities = itemQuantities;
    }

    public boolean containsItem(int itemId) {
        return getItemQuantities().get(itemId) != null;
    }

    public Map<Integer, Integer> getItemQuantities() {
        return itemQuantities;
    }
}
