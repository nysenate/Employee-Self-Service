package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/application")
public class TravelApplicationCtrl extends BaseRestApiCtrl {

    @Autowired private TravelApplicationService travelApplicationService;

    @RequestMapping(value = "/{id}")
    public BaseResponse getTravelAppById(@PathVariable String id) {
        TravelApplication app = travelApplicationService.getTravelApplication(UUID.fromString(id));
        TravelApplicationView appView = new DetailedTravelApplicationView(app);
        return new ViewObjectResponse(appView);
    }

    @RequestMapping(value = "/traveler/{travelerId}")
    public BaseResponse getActiveTravelApps(@PathVariable int travelerId) {
        List<TravelApplication> apps = travelApplicationService.getActiveTravelApplications(travelerId);
        List<TravelApplicationView> appViews = apps.stream()
                    .map(DetailedTravelApplicationView::new)
                    .collect(Collectors.toList());
        return ListViewResponse.of(appViews);
    }

}
