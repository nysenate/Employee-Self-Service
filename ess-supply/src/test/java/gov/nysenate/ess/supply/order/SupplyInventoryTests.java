package gov.nysenate.ess.supply.order;

import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.inventory.service.SupplyInventoryService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class SupplyInventoryTests extends SupplyTests {

    @Autowired
    private SupplyInventoryService inventory;

    @Test
    public void inventoryShouldInitialize() {
        assertNotNull(inventory.getCurrentInventory());
//        assertTrue(inventory.getCurrentInventory().size() > 0);
    }

}
