package gov.nysenate.ess.supply.integration.item;

import com.google.common.collect.Sets;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.supply.item.dao.SupplyItemDao;
import gov.nysenate.ess.supply.item.model.SupplyItem;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.Assert.*;

@Ignore
@Category(IntegrationTest.class)
public class SupplyItemDaoIT extends BaseTest {

    @Autowired
    private SupplyItemDao itemDao;

    private void assertItemRestrictionsInitialized(Set<SupplyItem> items) {
        for (SupplyItem item: items) {
            if (item.getId() == 1542) {
                assertTrue(item.isRestricted());
            }
        }
    }

    @Test
    public void canGetItems() {
        Set<SupplyItem> items = itemDao.getSupplyItems();
        assertTrue(items.size() > 0);
        assertItemRestrictionsInitialized(items);
    }

    @Test
    public void canGetItemsById() {
        Set<SupplyItem> items = itemDao.getItemsByIds(Sets.newHashSet(1542, 111));
        assertTrue(items.size() > 0);
        assertItemRestrictionsInitialized(items);
    }

    @Test
    public void getByIdShouldInitializeItemRestriction() {
        SupplyItem item = itemDao.getItemById(1542);
        assertTrue(item.isRestricted());
    }

    @Test
    public void canGetExpendableItem() {
        SupplyItem actual = itemDao.getItemById(111);
    }

    @Test
    public void canGetNonExpendableItem() {
        SupplyItem item = itemDao.getItemById(1815);
    }

    @Test
    public void canGetInactiveItem() {
        SupplyItem item = itemDao.getItemById(904);
    }

    @Test
    public void descriptionShouldPrefer_DeCommdtyEssSupply_field() {
        // Item 111 has both decommdty and decommdtyesssupply description fields.
        SupplyItem item = itemDao.getItemById(111);
        assertEquals("LABELING AND COVER UP TAPE", item.getDescription());
    }

    @Test
    public void descriptionShouldUse_Decommodityf_If_DeCommdtyEssSupply_IsNull() {
        SupplyItem item = itemDao.getItemById(904);
        assertEquals("COPYHOLDER METAL (STENO BOOK)", item.getDescription());
    }

    @Test
    public void if_cdsensuppieditem_isNullDefaultToOrderedBySupply() {
        SupplyItem item = itemDao.getItemById(4700);
        assertTrue(item.isExpendable());
        assertTrue(item.requiresSynchronization());
    }

    @Test
    public void if_cdspecpermvisible_isNullDefaultToVisible() {
        SupplyItem item = itemDao.getItemById(4700);
        assertTrue(item.isVisible());
    }

    @Test
    public void if_cdspecpermreq_isNullDefaultToNotSpecialRequest() {
        SupplyItem item = itemDao.getItemById(4700);
        assertFalse(item.isSpecialRequest());
    }

    @Test
    public void reconciliationPageNum_defaultsTo2() {
        SupplyItem item = itemDao.getItemById(4700);
        assertEquals(2, item.getReconciliationPage());
    }

    // Cant test default value when cdstockitem is null, no values in the database.
}
