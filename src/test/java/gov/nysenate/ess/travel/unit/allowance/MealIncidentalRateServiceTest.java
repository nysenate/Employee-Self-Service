package gov.nysenate.ess.travel.unit.allowance;


import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.travel.allowance.gsa.service.MealIncidentalRatesService;
import gov.nysenate.ess.web.SillyTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(SillyTest.class)
public class MealIncidentalRateServiceTest extends BaseTest{

    @Autowired MealIncidentalRatesService service;

    @Test
    public void scrapeAndOrUpdate(){
        service.scrapeAndUpdate();
    }
}
