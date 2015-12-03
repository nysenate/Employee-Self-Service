package gov.nysenate.ess.supply.inventory.service;

import java.util.Map;

public interface SupplyInventoryService {

    Map<Integer, Integer> getCurrentInventory();
}
