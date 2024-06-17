package gov.nysenate.ess.travel;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.travel.provider.miles.MileageAllowanceService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(IntegrationTest.class)
public class MileageAllowanceServiceIT extends BaseTest {
    @Autowired
    private MileageAllowanceService service;

    @Test
    public void testScraping() {
        Assert.assertNotNull("Could not parse reimbursement rates,", service.scrapeCurrentMileageRate());
    }
}
