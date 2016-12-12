package gov.nysenate.ess.supply.integration.item;

import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.supply.allowance.ItemVisibility;
import gov.nysenate.ess.supply.item.model.*;
import gov.nysenate.ess.supply.item.dao.OracleSupplyItemDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
    public void canGetExpendableItemById() {
        SupplyItem expected = new SupplyItem(111, "K1",  "LABELING AND COVER UP TAPE", new ItemStatus(true, true),
                                             new Category("KORECTYPE"), new ItemAllowance(2, 4), new ItemUnit("1", 1), ItemVisibility.VISIBLE);
        SupplyItem actual = itemDao.getItemById(111);
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void canGetNonExpendableItemById() {

    }

    // TODO: getting items should get senate supplied items, both stock item and senatesupplied items.
    // TODO: can get inactive item by id
    // TODO: can get non expendable items by id


    // TODO: Should i synch this item?
    //      is it tracked in sfms?
    //      ALSO is it expendable?
    //          If item is changed in DB as an order is placed it should sync correctly.
}
