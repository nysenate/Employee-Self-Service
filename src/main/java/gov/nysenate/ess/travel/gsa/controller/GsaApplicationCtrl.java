package gov.nysenate.ess.travel.gsa.controller;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.travel.application.model.GsaAllowance;
import gov.nysenate.ess.travel.application.model.Itinerary;
import gov.nysenate.ess.travel.application.view.GsaAllowanceView;
import gov.nysenate.ess.travel.application.view.ItineraryView;
import gov.nysenate.ess.travel.gsa.service.GsaAllowanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "travel/gsa/travel-gsa")
public class GsaApplicationCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(GsaApplicationCtrl.class);

    @Autowired private GsaAllowanceService gsaAllowanceService;

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse searchGsa(@RequestBody ItineraryView itineraryView) {
        Itinerary itinerary = itineraryView.toItinerary();

        GsaAllowance gsaAllowance = gsaAllowanceService.computeAllowance(itinerary);
        GsaAllowanceView gsaAllowanceView = new GsaAllowanceView(gsaAllowance);

        List<GsaAllowanceView> list = Arrays.asList(gsaAllowanceView);
        return ListViewResponse.of(list);
    }
}
