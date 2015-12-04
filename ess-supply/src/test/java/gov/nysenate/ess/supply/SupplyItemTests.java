package gov.nysenate.ess.supply;

import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.item.service.SupplyItemService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class SupplyItemTests extends SupplyTests {

    @Autowired
    private SupplyItemService supplyItemService;

    @Autowired
    private TestUtils testUtils;

    @Before
    public void before() {
        testUtils.resetInMemoryDaos();
    }

    @Test
    public void canGetAllSupplyItems() {
        List<SupplyItem> items = supplyItemService.getSupplyItems();
        assertTrue(items.size() > 0);
    }
}
