package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.auth.CorePermission;
import gov.nysenate.ess.core.model.auth.CorePermissionObject;
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
        checkPermission(new CorePermission(app.getTraveler().getEmployeeId(), CorePermissionObject.TRAVEL_APPLICATION, RequestMethod.GET));
        TravelApplicationView appView = new TravelApplicationView(app);
        return new ViewObjectResponse(appView);
    }

    @RequestMapping(value = "/traveler/{travelerId}")
    public BaseResponse getActiveTravelApps(@PathVariable int travelerId) {
        checkPermission(new CorePermission(travelerId, CorePermissionObject.TRAVEL_APPLICATION, RequestMethod.GET));
        List<TravelApplication> apps = travelApplicationService.getActiveTravelApplications(travelerId);
        List<TravelApplicationView> appViews = apps.stream()
                    .map(TravelApplicationView::new)
                    .collect(Collectors.toList());
        return ListViewResponse.of(appViews);
    }
}
