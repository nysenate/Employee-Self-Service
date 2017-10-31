package gov.nysenate.ess.travel.allowance.transportation.controller;

import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.travel.application.model.Itinerary;
import gov.nysenate.ess.travel.allowance.transportation.TransportationAllowance;
import gov.nysenate.ess.travel.application.view.ItineraryView;
import gov.nysenate.ess.travel.allowance.transportation.TransportationAllowanceView;
import gov.nysenate.ess.travel.allowance.transportation.TransportationAllowanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "travel/transportation-allowance")
public class TransportationAllowanceCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(TransportationAllowanceCtrl.class);

    @Autowired private TransportationAllowanceService transportationAllowanceService;

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ViewObjectResponse<TransportationAllowanceView> calculateTravelAllowance(@RequestBody ItineraryView itineraryView) {
        Itinerary itinerary = itineraryView.toItinerary();

        TransportationAllowance transportationAllowance = transportationAllowanceService.updateTravelAllowance(itinerary);
        TransportationAllowanceView transportationAllowanceView = new TransportationAllowanceView(transportationAllowance);

        return new ViewObjectResponse<>(transportationAllowanceView);
    }
}
