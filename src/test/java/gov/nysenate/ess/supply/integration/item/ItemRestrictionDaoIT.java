package gov.nysenate.ess.supply.integration.item;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.supply.item.dao.ItemRestrictionDao;
import gov.nysenate.ess.supply.item.model.ItemRestriction;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@org.junit.experimental.categories.Category(IntegrationTest.class)
public class ItemRestrictionDaoIT extends BaseTest {

    @Autowired private ItemRestrictionDao itemRestrictionDao;

    @Test
    public void canGetItemRestrictions() {
        ItemRestriction restriction = itemRestrictionDao.forItem(1542);
    }
}
