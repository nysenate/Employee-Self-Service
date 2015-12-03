package gov.nysenate.ess.supply.inventory.service;

import java.util.Map;

public interface InventoryService {

    Map<Integer, Integer> getCurrentInventory();
}
