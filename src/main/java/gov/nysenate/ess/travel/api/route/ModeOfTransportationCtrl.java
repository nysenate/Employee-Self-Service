package gov.nysenate.ess.travel.api.route;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.travel.request.route.MethodOfTravel;
import gov.nysenate.ess.travel.request.route.ModeOfTransportation;
import gov.nysenate.ess.travel.request.route.ModeOfTransportationView;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/mode-of-transportation")
public class ModeOfTransportationCtrl extends BaseRestApiCtrl {

    @RequestMapping(value = "", method = RequestMethod.GET)
    public BaseResponse getMethodsOfTravel() {
        List<ModeOfTransportation> mots = new ArrayList<>();
        mots.add(ModeOfTransportation.PERSONAL_AUTO);
        mots.add(ModeOfTransportation.SENATE_VEHICLE);
        mots.add(ModeOfTransportation.CARPOOL);
        mots.add(ModeOfTransportation.TRAIN);
        mots.add(ModeOfTransportation.AIRPLANE);
        mots.add(new ModeOfTransportation(MethodOfTravel.OTHER, ""));
        return ListViewResponse.of(mots.stream().map(ModeOfTransportationView::new).collect(Collectors.toList()));
    }
}
