package gov.nysenate.ess.travel.route;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.util.LimitOffset;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
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
