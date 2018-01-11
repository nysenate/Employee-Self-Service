package gov.nysenate.ess.travel.allowance.mileage.controller;

import com.google.maps.errors.ApiException;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.travel.allowance.mileage.model.MileageAllowance;
import gov.nysenate.ess.travel.allowance.mileage.model.MileageAllowanceView;
import gov.nysenate.ess.travel.application.model.Itinerary;
import gov.nysenate.ess.travel.application.view.ItineraryView;
import gov.nysenate.ess.travel.allowance.mileage.service.MileageAllowanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "travel/mileage-allowance")
public class MileageAllowanceCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(MileageAllowanceCtrl.class);

    @Autowired private MileageAllowanceService mileageAllowanceService;

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse calculateMileageAllowance(@RequestBody ItineraryView itineraryView) throws InterruptedException, ApiException, IOException {
        Itinerary itinerary = itineraryView.toItinerary();
        MileageAllowance allowance = mileageAllowanceService.calculateMileageAllowance(itinerary);
        return new ViewObjectResponse<>(new MileageAllowanceView(allowance));
    }
}
