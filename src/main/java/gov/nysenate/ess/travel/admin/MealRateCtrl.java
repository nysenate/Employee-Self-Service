package gov.nysenate.ess.travel.admin;

import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.travel.provider.gsa.meal.MealRatesService;
import gov.nysenate.ess.travel.provider.gsa.meal.MealRatesView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(BaseRestApiCtrl.ADMIN_REST_PATH + "/travel/meals")
public class MealRateCtrl {

    @Autowired
    MealRatesService mealRatesService;

    @RequestMapping(value = "", produces = "application/json")
    public MealRatesView updateMealRates() throws IOException {
       return mealRatesService.scrapeAndUpdate();
    }
}
