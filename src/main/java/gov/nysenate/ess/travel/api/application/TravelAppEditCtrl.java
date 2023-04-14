package gov.nysenate.ess.travel.api.application;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.view.DetailedEmployeeView;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.allowedtravelers.AllowedTravelersService;
import gov.nysenate.ess.travel.employee.TravelEmployee;
import gov.nysenate.ess.travel.employee.TravelEmployeeService;
import gov.nysenate.ess.travel.request.amendment.Amendment;
import gov.nysenate.ess.travel.request.app.TravelAppUpdateService;
import gov.nysenate.ess.travel.request.app.TravelApplication;
import gov.nysenate.ess.travel.request.app.TravelApplicationService;
import gov.nysenate.ess.travel.request.route.RouteViewValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/application")
public class TravelAppEditCtrl extends BaseRestApiCtrl {

    @Autowired private TravelApplicationService appService;
    @Autowired private RouteViewValidator routeViewValidator;
    @Autowired private TravelAppUpdateService appUpdateService;
    @Autowired private AllowedTravelersService allowedTravelersService;
    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private TravelEmployeeService travelEmployeeService;

    /**
     * Call this when starting to edit an application.
     * <p>
     * It returns an TravelAppEditDto which contains traveler and amendment fields which
     * should be modified when making edits.
     * - The traveler fields is an EmployeeView of the current traveler.
     * - The Amendment field is an AmendmentView. It is mostly a copy of the most recent
     * amendment but its Version is updated.
     *
     * @param appId
     * @return
     */
    @RequestMapping(value = "/edit/{appId}", method = RequestMethod.GET)
    public BaseResponse editApplication(@PathVariable int appId, @RequestParam String role) {
        TravelApplication app = appService.getTravelApplication(appId);
        // Check the logged in user is allowed to modify this app.
        checkTravelAppPermission(app, RequestMethod.POST);

        // The amendment to be edited is copied from the latest amendment and the version is incremented.
        Amendment editAmd = new Amendment.Builder(app.activeAmendment())
                .withVersion(app.activeAmendment().version().next())
                .build();

        TravelAppEditDto editDto = new TravelAppEditDto(
                new DetailedEmployeeView(app.getTraveler()),
                new AmendmentView(editAmd),
                app.getTravelerDeptHeadEmpId());

        Set<Employee> allowedTravelerEmps = allowedTravelersService.forEmp(app.getTraveler());
        Set<TravelEmployee> allowedTravelers = travelEmployeeService.getTravelEmployees(allowedTravelerEmps);
        editDto.setAllowedTravelerViews(allowedTravelers);
        return new ViewObjectResponse<>(editDto);
    }

    @RequestMapping(value = "/edit/{appId}", method = RequestMethod.POST)
    public BaseResponse saveEditedApplication(@PathVariable int appId,
                                              @RequestBody TravelAppEditDto appDto) {
        TravelApplication app = appService.getTravelApplication(appId);
        // Check the logged in user is allowed to modify this app
        checkTravelAppPermission(app, RequestMethod.POST);

        Amendment amd = appDto.getAmendment().toAmendment();
        Employee user = employeeInfoService.getEmployee(getSubjectEmployeeId());
        appUpdateService.editTravelApp(app.getAppId(), amd, user);

        return new SimpleResponse(true, "Edits saved", "");
    }

    @RequestMapping(value = "/edit/resubmit/{appId}", method = RequestMethod.POST)
    public BaseResponse saveAndResubmitEditedApplication(@PathVariable int appId,
                                              @RequestBody TravelAppEditDto appDto) {
        TravelApplication app = appService.getTravelApplication(appId);
        // Check the logged in user is allowed to modify this app
        checkTravelAppPermission(app, RequestMethod.POST);

        Amendment amd = appDto.getAmendment().toAmendment();
        Employee user = employeeInfoService.getEmployee(getSubjectEmployeeId());
        appUpdateService.resubmitApp(app.getAppId(), amd, user);

        return new SimpleResponse(true, "Edits saved", "");
    }

    /**
     * Fully populates the Route, MealPerDiems, and LodgingPerDiems on the Amendment being edited.
     *
     * @param appId
     * @param appDto
     * @return A TravelAppEditDto with the Amendment's Route, MealPerDiems, and LodgingPerDiems calculated.
     */
    @RequestMapping(value = "/edit/{appId}/route", method = RequestMethod.POST)
    public BaseResponse updateRoute(@PathVariable int appId,
                                    @RequestBody TravelAppEditDto appDto) {
        TravelApplication app = appService.getTravelApplication(appId);
        checkTravelAppPermission(app, RequestMethod.POST);

        routeViewValidator.validateTravelDates(appDto.getAmendment().getRoute());
        Amendment amd = appUpdateService.updateRoute(
                appDto.getAmendment().toAmendment(),
                appDto.getAmendment().getRoute().toRoute());

        // Create a new TravelAppEditDto to ensure allowedTravelers and eventTypes are populated correctly.
        appDto = new TravelAppEditDto(appDto.getTraveler(), new AmendmentView(amd), app.getTravelerDeptHeadEmpId());
        Set<Employee> allowedTravelerEmps = allowedTravelersService.forEmp(app.getTraveler());
        Set<TravelEmployee> allowedTravelers = travelEmployeeService.getTravelEmployees(allowedTravelerEmps);
        appDto.setAllowedTravelerViews(allowedTravelers);
        return new ViewObjectResponse<>(appDto);
    }

    /**
     * Updates user allowances and per diems.
     *
     * @param appId
     * @param appDto
     * @return A TravelAppEditDto with the Amendment's allowances and per diems updated.
     */
    @RequestMapping(value = "/edit/{appId}/allowances", method = RequestMethod.POST)
    public BaseResponse updateAllowances(@PathVariable int appId,
                                         @RequestBody TravelAppEditDto appDto) {
        TravelApplication app = appService.getTravelApplication(appId);
        checkTravelAppPermission(app, RequestMethod.POST);

        Amendment amd = appDto.getAmendment().toAmendment();
        amd = appUpdateService.updateAllowances(amd, appDto.getAmendment().getAllowances().toAllowances());
        amd = appUpdateService.updateMealPerDiems(amd, appDto.getAmendment().getMealPerDiems().toMealPerDiems());
        amd = appUpdateService.updateLodgingPerDiems(amd, appDto.getAmendment().getLodgingPerDiems().toLodgingPerDiems());
        amd = appUpdateService.updateMileagePerDiems(amd, appDto.getAmendment().getRoute().getMileagePerDiems().toMileagePerDiems());

        // Create a new TravelAppEditDto to ensure allowedTravelers and eventTypes are populated correctly.
        appDto = new TravelAppEditDto(appDto.getTraveler(), new AmendmentView(amd), app.getTravelerDeptHeadEmpId());
        Set<Employee> allowedTravelerEmps = allowedTravelersService.forEmp(app.getTraveler());
        Set<TravelEmployee> allowedTravelers = travelEmployeeService.getTravelEmployees(allowedTravelerEmps);
        appDto.setAllowedTravelerViews(allowedTravelers);
        return new ViewObjectResponse<>(appDto);
    }
}
