package gov.nysenate.ess.travel;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.travelallowance.IrsRateService;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class IrsRateServiceTest extends BaseTest {

    @Test
    public void scrapeIrsSite() throws IOException {
        IrsRateService irsRateService = new IrsRateService();
        double val = irsRateService.webScrapeIrsRate();
        assertEquals(val + "", "53.5");
    }
}