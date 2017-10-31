package gov.nysenate.ess.travel.allowance.mileage.controller;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.travel.allowance.mileage.MileageAllowanceView;
import gov.nysenate.ess.travel.application.model.Itinerary;
import gov.nysenate.ess.travel.application.view.ItineraryView;
import gov.nysenate.ess.travel.allowance.mileage.MileageAllowanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "travel/transportation-allowance")
public class MileageAllowanceCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(MileageAllowanceCtrl.class);

    @Autowired private MileageAllowanceService mileageAllowanceService;

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse calculateTravelAllowance(@RequestBody ItineraryView itineraryView) {
        Itinerary itinerary = itineraryView.toItinerary();

        BigDecimal mileageAllowance = mileageAllowanceService.calculateMileageAllowance(itinerary);
        return new ViewObjectResponse<>(new MileageAllowanceView(mileageAllowance));
    }
}
