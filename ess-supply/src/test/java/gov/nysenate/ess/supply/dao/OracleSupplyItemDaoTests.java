package gov.nysenate.ess.supply.dao;

import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.item.dao.OracleSupplyItemDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class OracleSupplyItemDaoTests extends SupplyTests {

    @Autowired
    private OracleSupplyItemDao itemDao;

    @Test
    public void canGetItems() {
        List<SupplyItem> items = itemDao.getSupplyItems(LimitOffset.ALL);
        assertTrue(items.size() > 0);
    }

    @Test
    public void canLimitResults() {
        List<SupplyItem> items = itemDao.getSupplyItems(LimitOffset.TWENTY_FIVE);
        assertTrue(items.size() == 25);
    }

    @Test
    public void canGetItemById() {
        SupplyItem actual = itemDao.getItemById(111);
        assertTrue(actual.getId() == 111);
    }
}
