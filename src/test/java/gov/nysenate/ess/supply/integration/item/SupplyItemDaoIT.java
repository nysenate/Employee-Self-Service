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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@org.junit.experimental.categories.Category(IntegrationTest.class)
public class SupplyItemDaoIT extends BaseTest {

    @Autowired
    private OracleSupplyItemDao itemDao;

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
        assertTrue(paginatedItems.getTotal() > 25);
    }

    @Test
    public void canGetItemById() {
        SupplyItem expected = new SupplyItem(111, "K1", "LABELING AND COVER UP TAPE",
                                             "1", new Category("KORECTYPE"), 2, 4, 1, ItemVisibility.VISIBLE, true);
        SupplyItem actual = itemDao.getItemById(111);
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void canGetItemsByCategory() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("BATTERIES"));
        categories.add(new Category("CLIPS"));
        PaginatedList<SupplyItem> items = itemDao.getSupplyItemsByCategories(categories, LimitOffset.ALL);
        items.getResults().forEach(item -> assertTrue(categories.contains(item.getCategory())));
    }

    /**
     * A few items inventory counts are not tracked in SFMS.
     * For these items, the field cdsensuppieditem = 'Y' and cdstockitem = 'N'.
     * These are still supply items and they should show up will all other supply items.
     */
    @Test
    public void getsExceptionalStockItems() {
        SupplyItem actual = itemDao.getItemById(2973);
        assertThat(actual.isInventoryTracked(), is(false));
    }
}
