package gov.nysenate.ess.supply.inventory.dao;

import java.util.Map;

public interface InventoryDao {

    Map<Integer, Integer> getCurrentInventory();
}
