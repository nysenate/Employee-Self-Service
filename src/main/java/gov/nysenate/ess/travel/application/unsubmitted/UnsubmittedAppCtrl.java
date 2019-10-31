package gov.nysenate.ess.travel.application.unsubmitted;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.travel.application.*;
import gov.nysenate.ess.travel.application.allowances.AllowancesView;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingPerDiemsView;
import gov.nysenate.ess.travel.application.allowances.meal.MealPerDiemsView;
import gov.nysenate.ess.travel.application.allowances.mileage.MileagePerDiemsView;
import gov.nysenate.ess.travel.application.route.Route;
import gov.nysenate.ess.travel.application.route.RouteService;
import gov.nysenate.ess.travel.application.route.RouteView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/unsubmitted")
public class UnsubmittedAppCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(UnsubmittedAppCtrl.class);
    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private UnsubmittedAppDao unsubmittedAppDao;
    @Autowired private TravelApplicationService travelApplicationService;
    @Autowired private RouteService routeService;
    @Autowired private AmendmentService amendmentService;

    /**
     * Get an unsubmitted app API
     * --------------------------
     * Get the current unsubmitted app for the user.
     * <p>
     * Usage:   (GET) /api/v1/travel/unsubmitted
     * <p>
     * Request Params:
     *
     * @param userId     Integer - required - the employee id of the logged in user.
     * @return {@link TravelApplicationView}
     * @throws IOException
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public BaseResponse getUnsubmittedApps(@RequestParam int userId) throws IOException {
        TravelApplicationView appView;
        Optional<TravelApplicationView> viewOpt = unsubmittedAppDao.find(userId);
        Employee user = employeeInfoService.getEmployee(getSubjectEmployeeId());
        if (viewOpt.isPresent()) {
            appView = viewOpt.get();
            checkTravelAppPermission(viewOpt.get().toTravelApplication(), RequestMethod.GET);
        } else {
            appView = new TravelApplicationView(new TravelApplication(user, user));
            checkTravelAppPermission(appView.toTravelApplication(), RequestMethod.GET);
            unsubmittedAppDao.save(userId, appView);
        }

        Set<Employee> allEmps = employeeInfoService.getAllEmployees(true);
        Set<Employee> rchEmployees = allEmps.stream()
                .filter(e -> Optional.ofNullable(e.getRespCenterHeadCode()).orElse("").equals(user.getRespCenterHeadCode()))
                .collect(Collectors.toSet());
        return new ViewObjectResponse<>(new NewApplicationDto(appView, rchEmployees));
    }

    /**
     * Delete an unsubmitted app API
     * -----------------------------
     * Deletes the currently saved unsubmitted app for a given user.
     * This effectively resets the application for starting over.
     * <p>
     * Usage:   (DELETE) /api/v1/travel/unsubmitted
     * <p>
     * Request Params
     *
     * @param userId     Integer - required - the employee id of the logged in user.
     * @throws IOException
     */
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public void deleteUnsubmittedApp(@RequestParam int userId) throws IOException {
        TravelApplicationView applicationView = findApp(userId);
        checkTravelAppPermission(applicationView.toTravelApplication(), RequestMethod.POST);
        unsubmittedAppDao.delete(userId);
    }

    /**
     * Patch an unsubmitted app API
     * ----------------------------
     * Updates one or more fields of an unsubmitted app.
     * <p>
     * Usage:   (PATCH) /api/v1/travel/unsubmitted
     * <p>
     * Request Params:
     *
     * @param userId     Integer - required - the employee id of the logged in user.
     *                   <p>
     *                   Body:
     * @param patches    Map of patch keys to patch values. Patch key represents a field to be updated with the patch value.
     * @return {@link TravelApplicationView} updated with patches.
     * @throws IOException
     */
    @RequestMapping(value = "", method = RequestMethod.PATCH)
    public BaseResponse patchUnsubmittedApp(@RequestParam int userId,
                                            @RequestBody Map<String, String> patches) throws IOException {
        TravelApplicationView view = findApp(userId);
        TravelApplication app = view.toTravelApplication();
        checkTravelAppPermission(app, RequestMethod.POST);
        Employee user = employeeInfoService.getEmployee(getSubjectEmployeeId());

        // Perform all updates specified in the patch.
        for (Map.Entry<String, String> patch : patches.entrySet()) {
            switch (patch.getKey()) {
                case "traveler":
                    int travelerEmpId = Integer.valueOf(patch.getValue());
                    if (travelerEmpId != app.getTraveler().getEmployeeId()) {
                        app.setTraveler(employeeInfoService.getEmployee(travelerEmpId));
                    }
                    break;
                case "purposeOfTravel":
                    PurposeOfTravelView potView = OutputUtils.jsonToObject(patch.getValue(), PurposeOfTravelView.class);
                    app.activeAmendment().setPurposeOfTravel(potView.toPurposeOfTravel());
                    break;
                case "outbound":
                    RouteView outboundRouteView = OutputUtils.jsonToObject(patch.getValue(), RouteView.class);
                    Route outboundRoute = outboundRouteView.toRoute();
                    if (!outboundRoute.equals(app.activeAmendment().route())) {
                        app.activeAmendment().setOutboundRoute(outboundRoute);
                    }
                    break;
                case "route":
                    RouteView routeView = OutputUtils.jsonToObject(patch.getValue(), RouteView.class);
                    Route fullRoute = routeService.createRoute(routeView.toRoute());
                    amendmentService.setRoute(app.activeAmendment(), fullRoute);
                    break;
                case "allowances":
                    AllowancesView allowancesView = OutputUtils.jsonToObject(patch.getValue(), AllowancesView.class);
                    app.activeAmendment().setAllowances(allowancesView.toAllowances());
                    break;
                case "mealPerDiems":
                    MealPerDiemsView mealPerDiemsView = OutputUtils.jsonToObject(patch.getValue(), MealPerDiemsView.class);
                    app.activeAmendment().setMealPerDiems(mealPerDiemsView.toMealPerDiems());
                    break;
                case "lodgingPerDiems":
                    LodgingPerDiemsView lodgingPerDiemsView = OutputUtils.jsonToObject(patch.getValue(), LodgingPerDiemsView.class);
                    app.activeAmendment().setLodingPerDiems(lodgingPerDiemsView.toLodgingPerDiems());
                    break;
                case "mileagePerDiems":
                    MileagePerDiemsView mileagePerDiemView = OutputUtils.jsonToObject(patch.getValue(), MileagePerDiemsView.class);
                    travelApplicationService.updateMileagePerDiems(app, mileagePerDiemView.toMileagePerDiems());
                    break;
                default:
                    logger.info("Call to travel application patch API did not contain a valid patch key. Patches were: " + patches.toString());
            }
        }

        TravelApplicationView appView = new TravelApplicationView(app);
        // Save after all changes are applied.
        unsubmittedAppDao.save(user.getEmployeeId(), appView);

        return new ViewObjectResponse<>(appView);
    }

    /**
     * Submit unsubmitted app API
     * --------------------------
     * Submit an unsubmitted app.
     * <p>
     * Request Params:
     *
     * @param userId     Integer - required - the employee id of the logged in user.
     * @return {@link TravelApplicationView}
     * @throws IOException
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public BaseResponse submitApp(@RequestParam int userId) throws IOException {
        TravelApplicationView view = findApp(userId);
        TravelApplication app = view.toTravelApplication();
        checkTravelAppPermission(app, RequestMethod.POST);
        Employee user = employeeInfoService.getEmployee(getSubjectEmployeeId());

        app = travelApplicationService.submitTravelApplication(app, user);
        unsubmittedAppDao.delete(userId);

        return new ViewObjectResponse<>(new TravelApplicationView(app));
    }

    private TravelApplicationView findApp(int userId) throws IOException {
        return unsubmittedAppDao.find(userId)
                .orElseThrow(invalidUserIdOrTravelerId(userId));
    }

    private Supplier<InvalidRequestParamEx> invalidUserIdOrTravelerId(int userId) {
        return () -> new InvalidRequestParamEx(String.valueOf(userId), "userId", "int",
                "No Unsubmitted travel app found with provided userId");
    }
}
