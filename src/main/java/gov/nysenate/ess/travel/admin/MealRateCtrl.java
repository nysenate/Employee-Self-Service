package gov.nysenate.ess.travel.admin;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.travel.provider.dod.DodMealTierView;
import gov.nysenate.ess.travel.provider.dod.DodService;
import gov.nysenate.ess.travel.provider.gsa.meal.MealRatesService;
import gov.nysenate.ess.travel.provider.gsa.meal.MealRatesView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping(BaseRestApiCtrl.ADMIN_REST_PATH + "/travel/meals")
public class MealRateCtrl {

    private static final Logger logger = LoggerFactory.getLogger(MealRateCtrl.class);

    @Autowired
    MealRatesService mealRatesService;

    @Autowired
    DodService dodService;

    @RequestMapping(value = "", produces = "application/json")
    public BaseResponse updateMealRates() throws IOException {
       return new ViewObjectResponse<>(new MealRatesView(mealRatesService.scrapeAndUpdate()));
    }

    @RequestMapping(value = "/dod", produces = "application/json")
    public DodMealTierView scrapeDodMealRates(@RequestParam String country,
                                              @RequestParam String city,
                                              @RequestParam String travelDate) throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        return  new DodMealTierView(dodService.getNonConusMealInfo(country, city, LocalDate.parse(travelDate, formatter)));
    }
}
