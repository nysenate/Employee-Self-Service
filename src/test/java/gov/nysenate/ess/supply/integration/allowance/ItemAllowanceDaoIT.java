package gov.nysenate.ess.supply.integration.allowance;

import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.BaseTests;
import gov.nysenate.ess.supply.allowance.ItemAllowance;
import gov.nysenate.ess.supply.allowance.dao.ItemAllowanceDao;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

@Category(IntegrationTest.class)
public class ItemAllowanceDaoIT extends BaseTests {

    @Autowired private ItemAllowanceDao allowanceDao;

    @Test
    public void canGetAllowances() {
        Set<ItemAllowance> allowances = allowanceDao.getItemAllowances(new LocationId("A42FB", 'W'));
        assertThat(allowances.size(), is(greaterThan(1)));
    }
}
