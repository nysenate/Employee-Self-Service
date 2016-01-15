package gov.nysenate.ess.supply.integration;

import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.item.service.SupplyItemService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class SupplyItemDaoTests extends SupplyTests {

    @Autowired
    private SupplyItemService itemService;

    @Test
    public void canGetItems() {
        List<SupplyItem> items = itemService.getSupplyItems();
        assertTrue(items.size() > 0);
    }

    @Test
    public void canGetItemById() {
        SupplyItem actual = itemService.getItemById(111);
        assertTrue(actual.getId() == 111);
    }
}
