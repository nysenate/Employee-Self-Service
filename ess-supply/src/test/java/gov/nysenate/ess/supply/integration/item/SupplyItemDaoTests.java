package gov.nysenate.ess.supply.integration.item;

import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.item.dao.OracleSupplyItemDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class SupplyItemDaoTests extends SupplyTests {

    @Autowired
    private OracleSupplyItemDao itemDao;

    private static final int TEST_ITEM_ID = 111;

    @Test
    public void canGetItems() {
        PaginatedList<SupplyItem> items = itemDao.getSupplyItems(LimitOffset.ALL);
        assertTrue(items.getTotal() > 0);
        assertTrue(items.getResults().size() > 0);
    }

    @Test
    public void canLimitResults() {
        PaginatedList<SupplyItem> paginatedItems = itemDao.getSupplyItems(LimitOffset.TWENTY_FIVE);
        assertTrue(paginatedItems.getResults().size() == 25);
    }

    /**
     * NOTE: expected item with id 111 is randomly taken from database. May not exist in the future.
     */
    @Test
    public void canGetItemById() {
        // TODO: item name and suggested max qty will have to be adjusted when those values get added to the database.
        SupplyItem expected = new SupplyItem(111, "K1", "Item Name", "KO-REC-TYPE TAPE/1 LINE SIZE GR-23031",
                                             "1", "KORECTYPE", 2, 1);
        SupplyItem actual = itemDao.getItemById(TEST_ITEM_ID);
        assertThat(actual, equalTo(expected));
    }

}
