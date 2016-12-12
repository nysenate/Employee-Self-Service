package gov.nysenate.ess.supply.integration.item;

import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.supply.allowance.ItemVisibility;
import gov.nysenate.ess.supply.item.Category;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.item.dao.OracleSupplyItemDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@org.junit.experimental.categories.Category(IntegrationTest.class)
public class SupplyItemDaoIT extends BaseTest {

    @Autowired
    private OracleSupplyItemDao itemDao;

    @Test
    public void canGetItems() {
        Set<SupplyItem> items = itemDao.getSupplyItems();
        assertTrue(items.size() > 0);
    }

    @Test
    public void canGetItemById() {
        SupplyItem expected = new SupplyItem(111, "K1", "LABELING AND COVER UP TAPE",
                                             "1", new Category("KORECTYPE"), 2, 4, 1, ItemVisibility.VISIBLE, true);
        SupplyItem actual = itemDao.getItemById(111);
        assertThat(actual, equalTo(expected));
    }

    // TODO: getting items should get senate supplied items, both stock item and senatesupplied items.
    // TODO: can get inactive item by id
    // TODO: can get non expendable items by id
}
