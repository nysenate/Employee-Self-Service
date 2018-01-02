package gov.nysenate.ess.travel.unit.allowance;


import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.travel.allowance.gsa.service.MealRatesService;
import gov.nysenate.ess.web.SillyTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@Category(SillyTest.class)
public class MealRateServiceTest extends BaseTest{

    @Autowired MealRatesService service;

    @Test
    public void scrapeAndOrUpdate() throws IOException {
        service.scrapeAndUpdate();
    }
}
