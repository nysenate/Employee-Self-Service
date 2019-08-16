package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.travel.authorization.permission.TravelPermissionBuilder;
import gov.nysenate.ess.travel.authorization.permission.TravelPermissionObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/application")
public class TravelApplicationCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(TravelApplicationCtrl.class);
    @Autowired private TravelApplicationService travelApplicationService;

    @RequestMapping(value = "/{appId}", method = RequestMethod.GET)
    public BaseResponse getTravelAppById(@PathVariable int appId) {
        TravelApplication app = travelApplicationService.getTravelApplication(appId);
        checkPermission(new TravelPermissionBuilder()
                .forEmpId(app.getTraveler().getEmployeeId())
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION)
                .forAction(RequestMethod.GET)
                .buildPermission());
        TravelApplicationView appView = new TravelApplicationView(app);
        return new ViewObjectResponse<>(appView);
    }

    @RequestMapping(value = "/traveler/{travelerId}")
    public BaseResponse getActiveTravelApps(@PathVariable int travelerId) {
        checkPermission(new TravelPermissionBuilder()
                .forEmpId(travelerId)
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION)
                .forAction(RequestMethod.GET)
                .buildPermission());

        List<TravelApplication> apps = travelApplicationService.selectTravelApplications(travelerId);
        List<TravelApplicationView> appViews = apps.stream()
                .map(TravelApplicationView::new)
                .collect(Collectors.toList());
        return ListViewResponse.of(appViews);
    }

    @RequestMapping(value = "/{appId}", method = RequestMethod.PUT)
    public BaseResponse saveTravelApp(@PathVariable int appId, @RequestBody TravelApplicationView appView) {
        TravelApplication app = travelApplicationService.getTravelApplication(appId);
        checkPermission(new TravelPermissionBuilder()
                .forEmpId(app.getTraveler().getEmployeeId())
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION)
                .forAction(RequestMethod.POST)
                .buildPermission());

        // TODO
        return null;
    }
}
