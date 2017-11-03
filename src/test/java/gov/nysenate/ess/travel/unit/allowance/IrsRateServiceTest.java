package gov.nysenate.ess.travel.unit.allowance;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.allowance.mileage.IrsRateService;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class IrsRateServiceTest extends BaseTest {

    // TODO: This should use mocks instead of really scraping the IRS website.

    @Test
    public void scrapeIrsSite() throws IOException {
        IrsRateService irsRateService = new IrsRateService();
        double val = irsRateService.webScrapeIrsRate();
        assertEquals(val + "", "53.5");
    }
}