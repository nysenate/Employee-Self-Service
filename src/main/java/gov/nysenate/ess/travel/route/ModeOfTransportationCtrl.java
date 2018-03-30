package gov.nysenate.ess.travel.route;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.util.LimitOffset;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/mode-of-transportation")
public class ModeOfTransportationCtrl extends BaseRestApiCtrl {

    @RequestMapping(value = "", method = RequestMethod.GET)
    public BaseResponse getModesOfTransportation() {
        List<String> mots = Arrays.stream(ModeOfTransportation.values())
                .map(ModeOfTransportation::getDisplayName)
                .collect(Collectors.toList());
        return ListViewResponse.ofStringList(mots, "result", mots.size(), LimitOffset.ALL);
    }

}
