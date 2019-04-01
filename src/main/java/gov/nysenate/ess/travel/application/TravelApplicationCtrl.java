package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.auth.CorePermission;
import gov.nysenate.ess.core.model.auth.CorePermissionObject;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.travel.application.allowances.AllowancesView;
import gov.nysenate.ess.travel.application.route.RouteView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/application")
public class TravelApplicationCtrl extends BaseRestApiCtrl {

    @Autowired private TravelApplicationService travelApplicationService;
    @Autowired private EmployeeInfoService employeeInfoService;

    @RequestMapping(value = "/{appId}", method = RequestMethod.GET)
    public BaseResponse getTravelAppById(@PathVariable int appId) {
        TravelApplication app = travelApplicationService.getTravelApplication(appId);
        checkPermission(new CorePermission(app.getTraveler().getEmployeeId(), CorePermissionObject.TRAVEL_APPLICATION, RequestMethod.GET));
        TravelApplicationView appView = new TravelApplicationView(app);
        return new ViewObjectResponse(appView);
    }

    @RequestMapping(value = "/{appId}", method = RequestMethod.DELETE)
    public void deleteTravelApp(@PathVariable int appId) {
        TravelApplication app = travelApplicationService.getTravelApplication(appId);
        checkPermission(new CorePermission(app.getTraveler().getEmployeeId(), CorePermissionObject.TRAVEL_APPLICATION, RequestMethod.POST));
        travelApplicationService.deleteTravelApplication(appId);
    }

    /**
     * Update a single field of a travel application.
     */
    @RequestMapping(value = "/{appId}", method = RequestMethod.PATCH)
    public BaseResponse patchTravelApplication(@PathVariable int appId, @RequestBody Map<String, String> patches) throws IOException {
        TravelApplication app = travelApplicationService.getTravelApplication(appId);
        checkPermission(new CorePermission(app.getTraveler().getEmployeeId(), CorePermissionObject.TRAVEL_APPLICATION, RequestMethod.POST));

        for (Map.Entry<String, String> patch : patches.entrySet()) {
            switch (patch.getKey()) {
                case "purposeOfTravel":
                    travelApplicationService.updatePurposeOfTravel(app, patch.getValue());
                    break;
                case "route":
                    travelApplicationService.updateRoute(app, OutputUtils.jsonToObject(patch.getValue(), RouteView.class));
                    break;
                case "allowances":
                    travelApplicationService.updateAllowances(app, OutputUtils.jsonToObject(patch.getValue(), AllowancesView.class));
                    break;
                case "action":
                    if (patch.getValue().equals("submit")) {
                        travelApplicationService.submitTravelApplication(app);
                    }

            }
        }
        TravelApplicationView appView = new TravelApplicationView(app);
        return new ViewObjectResponse(appView);
    }

    @RequestMapping(value = "")
    public BaseResponse getUncompletedApplication(@RequestParam int travelerId) {
        checkPermission(new CorePermission(travelerId, CorePermissionObject.TRAVEL_APPLICATION, RequestMethod.GET));

        TravelApplication app = travelApplicationService.uncompleteAppForTraveler(travelerId);
        return new ViewObjectResponse(new TravelApplicationView(app));
    }

    @RequestMapping(value = "/traveler/{travelerId}")
    public BaseResponse getActiveTravelApps(@PathVariable int travelerId) {
        checkPermission(new CorePermission(travelerId, CorePermissionObject.TRAVEL_APPLICATION, RequestMethod.GET));
        List<TravelApplication> apps = travelApplicationService.selectTravelApplications(travelerId);
        List<TravelApplicationView> appViews = apps.stream()
                .map(TravelApplicationView::new)
                .collect(Collectors.toList());
        return ListViewResponse.of(appViews);
    }
}
