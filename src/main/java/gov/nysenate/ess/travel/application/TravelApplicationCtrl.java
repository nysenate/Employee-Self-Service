package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.travel.application.allowances.AllowancesView;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingPerDiemsView;
import gov.nysenate.ess.travel.application.allowances.meal.MealPerDiemsView;
import gov.nysenate.ess.travel.application.allowances.mileage.MileagePerDiemsView;
import gov.nysenate.ess.travel.application.overrides.perdiem.PerDiemOverridesView;
import gov.nysenate.ess.travel.application.route.SimpleRouteView;
import gov.nysenate.ess.travel.authorization.permission.TravelPermissionBuilder;
import gov.nysenate.ess.travel.authorization.permission.TravelPermissionObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/application")
public class TravelApplicationCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(TravelApplicationCtrl.class);
    @Autowired private TravelApplicationService travelApplicationService;
    @Autowired private EmployeeInfoService employeeInfoService;

    @RequestMapping(value = "/{appId}", method = RequestMethod.GET)
    public BaseResponse getTravelAppById(@PathVariable int appId) {
        TravelApplication app = travelApplicationService.getTravelApplication(appId);
        checkPermission(new TravelPermissionBuilder()
                .forEmpId(app.getTraveler().getEmployeeId())
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION)
                .forAction(RequestMethod.GET)
                .buildPermission());
        SimpleTravelApplicationView appView = new SimpleTravelApplicationView(app);
        return new ViewObjectResponse<>(appView);
    }

    @RequestMapping(value = "/{appId}", method = RequestMethod.DELETE)
    public void deleteTravelApp(@PathVariable int appId) {
        TravelApplication app = travelApplicationService.getTravelApplication(appId);
        checkPermission(new TravelPermissionBuilder()
                .forEmpId(app.getTraveler().getEmployeeId())
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION)
                .forAction(RequestMethod.POST)
                .buildPermission());
        travelApplicationService.deleteTravelApplication(appId);
    }

    /**
     * Update a single field of a travel application.
     * Multiple updates can be sent in a single request. The app is saved once after all patches are applied.
     *
     * @param patches A map of changes to be made to the application. Key is a string describing what kind of update is contained in the value.
     *                Value is either the raw data (if a string) or an object serialized into a json String.
     */
    @RequestMapping(value = "/{appId}", method = RequestMethod.PATCH)
    public BaseResponse patchTravelApplication(@PathVariable int appId, @RequestBody Map<String, String> patches) throws IOException {
        TravelApplication app = travelApplicationService.getTravelApplication(appId);
        checkPermission(new TravelPermissionBuilder()
                .forEmpId(app.getTraveler().getEmployeeId())
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION)
                .forAction(RequestMethod.POST)
                .buildPermission());

        Employee user = employeeInfoService.getEmployee(getSubjectEmployeeId());

        // Perform all updates specified in the patch.
        for (Map.Entry<String, String> patch : patches.entrySet()) {
            switch (patch.getKey()) {
                case "purposeOfTravel":
                    travelApplicationService.updatePurposeOfTravel(app, patch.getValue());
                    break;
                case "route":
                    travelApplicationService.updateRoute(app, OutputUtils.jsonToObject(patch.getValue(), SimpleRouteView.class));
                    break;
                case "allowances":
                    travelApplicationService.updateAllowances(app, OutputUtils.jsonToObject(patch.getValue(), AllowancesView.class));
                    break;
                case "mealPerDiems":
                    travelApplicationService.updateMealPerDiems(app, OutputUtils.jsonToObject(patch.getValue(), MealPerDiemsView.class));
                    break;
                case "lodgingPerDiems":
                    travelApplicationService.updateLodgingPerDiems(app, OutputUtils.jsonToObject(patch.getValue(), LodgingPerDiemsView.class));
                    break;
                case "mileagePerDiems":
                    travelApplicationService.updateMileagePerDiems(app, OutputUtils.jsonToObject(patch.getValue(), MileagePerDiemsView.class));
                    break;
                case "perDiemOverrides":
                    travelApplicationService.updatePerDiemOverrides(app, OutputUtils.jsonToObject(patch.getValue(), PerDiemOverridesView.class));
                    break;
                case "action":
                    if (patch.getValue().equals("submit")) {
                        travelApplicationService.submitTravelApplication(app, user);
                    }
                default:
                    logger.info("Call to travel application patch API did not contain a valid patch key. Patches were: " + patches.toString());
            }
        }

        // Save after all changes are applied.
        travelApplicationService.saveTravelApplication(app, user);

        SimpleTravelApplicationView appView = new SimpleTravelApplicationView(app);
        return new ViewObjectResponse(appView);
    }

    @RequestMapping(value = "")
    public BaseResponse getUncompletedApplication(@RequestParam int travelerId) {
        checkPermission(new TravelPermissionBuilder()
                .forEmpId(travelerId)
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION)
                .forAction(RequestMethod.GET)
                .buildPermission());

        TravelApplication app = travelApplicationService.uncompleteAppForTraveler(travelerId);
        return new ViewObjectResponse(new SimpleTravelApplicationView(app));
    }

    @RequestMapping(value = "/traveler/{travelerId}")
    public BaseResponse getActiveTravelApps(@PathVariable int travelerId) {
        checkPermission(new TravelPermissionBuilder()
                .forEmpId(travelerId)
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION)
                .forAction(RequestMethod.GET)
                .buildPermission());

        List<TravelApplication> apps = travelApplicationService.selectTravelApplications(travelerId);
        List<SimpleTravelApplicationView> appViews = apps.stream()
                .map(SimpleTravelApplicationView::new)
                .collect(Collectors.toList());
        return ListViewResponse.of(appViews);
    }
}
