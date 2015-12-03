package gov.nysenate.ess.supply.inventory.service;

import gov.nysenate.ess.supply.inventory.dao.InventoryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EssSupplyInventoryService implements SupplyInventoryService {

    @Autowired private InventoryDao inventoryDao;

    @Override
    public Map<Integer, Integer> getCurrentInventory() {
        return inventoryDao.getCurrentInventory();
    }
}
