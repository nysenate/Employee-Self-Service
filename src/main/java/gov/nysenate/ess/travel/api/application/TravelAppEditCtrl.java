package gov.nysenate.ess.travel.api.application;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.employee.TravelEmployeeService;
import gov.nysenate.ess.travel.request.app.*;
import gov.nysenate.ess.travel.request.draft.Draft;
import gov.nysenate.ess.travel.request.draft.DraftView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/application")
public class TravelAppEditCtrl extends BaseRestApiCtrl {

    @Autowired private TravelApplicationService appService;
    @Autowired private TravelAppUpdateService appUpdateService;
    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private TravelEmployeeService travelEmployeeService;

    /**
     * Initializes the editing of an Application.
     * -----------------------------
     * This returns a Draft which is initialized to the current app data.
     * The draft can be modified and resubmitted to save the changes.
     * <p>
     * Usage:   (GET) /api/v1/travel/application/edit/{appId}
     * </p>
     */
    @RequestMapping(value = "/edit/{appId}", method = RequestMethod.GET)
    public BaseResponse editApplication(@PathVariable int appId) {
        TravelApplication app = appService.getTravelApplication(appId);
        // Check the logged in user is allowed to modify this app.
        checkTravelAppPermission(app, RequestMethod.POST);

        Draft draft = new Draft(getSubjectEmployeeId(), travelEmployeeService.getTravelEmployee(app.getTraveler()));
        draft.setTravelApplication(app);
        return new ViewObjectResponse<>(new DraftView(draft));
    }

    /**
     * Save edits to a Travel Application.
     * -----------------------------
     * <p>
     * Usage:   (POST) /api/v1/travel/application/edit/{appId}
     * </p>
     */
    @RequestMapping(value = "/edit/{appId}", method = RequestMethod.POST)
    public BaseResponse saveEditedApplication(@PathVariable int appId,
                                              @RequestBody DraftView draftView) {
        // Check the logged in user is allowed to modify this app
        TravelApplication originalApp = appService.getTravelApplication(appId);
        checkTravelAppPermission(originalApp, RequestMethod.POST);

        TravelApplication editedApp = draftView.toDraft().getTravelApplication();
        editedApp.setAppId(appId); // TODO hacky way around messed up views for now.
        editedApp.setStatus(originalApp.status());
        Employee user = employeeInfoService.getEmployee(getSubjectEmployeeId());
        appUpdateService.editTravelApp(appId, editedApp, user);
        return new SimpleResponse(true, "Edits saved", "");
    }

    /**
     * Save edits and resubmit a Travel Application.
     * -----------------------------
     * <p>
     * Usage:   (POST) /api/v1/travel/application/resubmit/{appId}
     * </p>
     */
    @RequestMapping(value = "/edit/resubmit/{appId}", method = RequestMethod.POST)
    public BaseResponse saveAndResubmitEditedApplication(@PathVariable int appId,
                                              @RequestBody DraftView draftView) {
        // Check the logged in user is allowed to modify this app
        TravelApplication originalApp = appService.getTravelApplication(appId);
        checkTravelAppPermission(originalApp, RequestMethod.POST);

        TravelApplication app = draftView.toDraft().getTravelApplication();
        // TODO hacky fix around messed up views. Ideally, these fields would be correct on the views.
        app.setAppId(originalApp.getAppId());
        app.setStatus(originalApp.status());

        Employee user = employeeInfoService.getEmployee(getSubjectEmployeeId());
        appUpdateService.resubmitApp(appId, app, user);

        return new SimpleResponse(true, "Edits saved", "");
    }

    @RequestMapping(value = "/edit/{appId}/cancel", method = RequestMethod.POST)
    public BaseResponse cancelTravelApp(@PathVariable int appId) {
        TravelApplication app = appService.getTravelApplication(appId);
        checkTravelAppPermission(app, RequestMethod.POST);

        app.setStatus(new TravelApplicationStatus(AppStatus.CANCELED));
        appService.saveApplication(app);

        return new ViewObjectResponse<>(new TravelApplicationView(app));
    }
}
