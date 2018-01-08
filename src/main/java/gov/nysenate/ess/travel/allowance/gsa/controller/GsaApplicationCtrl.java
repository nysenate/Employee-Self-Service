package gov.nysenate.ess.travel.allowance.gsa.controller;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.travel.allowance.gsa.model.LodgingAllowance;
import gov.nysenate.ess.travel.allowance.gsa.model.MealAllowance;
import gov.nysenate.ess.travel.allowance.gsa.view.LodgingAllowanceView;
import gov.nysenate.ess.travel.allowance.gsa.view.MealAllowanceView;
import gov.nysenate.ess.travel.application.model.Itinerary;
import gov.nysenate.ess.travel.application.view.ItineraryView;
import gov.nysenate.ess.travel.allowance.gsa.service.GsaAllowanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "travel/gsa/travel-gsa")
public class GsaApplicationCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(GsaApplicationCtrl.class);

    @Autowired private GsaAllowanceService gsaService;

    @RequestMapping(value = "/meals", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse getMealAllowance(@RequestBody ItineraryView itineraryView) throws IOException {
        Itinerary itinerary = itineraryView.toItinerary();
        MealAllowance mealAllowance = gsaService.calculateMealAllowance(itinerary);
        return new ViewObjectResponse<>(new MealAllowanceView(mealAllowance));
    }

    @RequestMapping(value = "/lodging", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse getLodgingAllowance(@RequestBody ItineraryView itineraryView) throws IOException {
        Itinerary itinerary = itineraryView.toItinerary();
        LodgingAllowance lodgingAllowance = gsaService.calculateLodging(itinerary);
        return new ViewObjectResponse<>(new LodgingAllowanceView(lodgingAllowance));
    }
}
