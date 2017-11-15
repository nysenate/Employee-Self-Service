package gov.nysenate.ess.travel.unit.allowance;


import gov.nysenate.ess.travel.allowance.gsa.service.MealIncidentalRatesService;
import gov.nysenate.ess.web.SillyTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(SillyTest.class)
public class MealIncidentalRateServiceTest {

    @Test
    public void blah(){
        MealIncidentalRatesService service = new MealIncidentalRatesService();
        service.scrapeAndUpdate();
    }
}
