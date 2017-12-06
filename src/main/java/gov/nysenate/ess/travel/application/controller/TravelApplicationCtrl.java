package gov.nysenate.ess.travel.application.controller;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.util.ShiroUtils;
import gov.nysenate.ess.travel.application.model.TravelApplication;
import gov.nysenate.ess.travel.application.model.TravelApplicationStatus;
import gov.nysenate.ess.travel.application.service.TravelApplicationService;
import gov.nysenate.ess.travel.application.view.NewTravelApplicationView;
import gov.nysenate.ess.travel.application.view.TravelApplicationView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/travel-application")
public class TravelApplicationCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(TravelApplicationCtrl.class);

    @Autowired private TravelApplicationService travelAppService;
    @Autowired private EmployeeInfoService employeeInfoService;

    /**
     * Submit a new travel application.
     */
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse submitTravelApplication(@RequestBody NewTravelApplicationView newTravelApp) {
        // TODO check permissions, subject has permission to create an app for newTravelApp.applicantEmpId.
        // - Verify logged in user can submit application for traveler
        TravelApplication app = newTravelApp.toTravelApplicationBuilder()
                .setTraveler(employeeInfoService.getEmployee(newTravelApp.getTravelerEmpId()))
                .setCreatedBy(employeeInfoService.getEmployee(ShiroUtils.getAuthenticatedEmpId()))
                .setCreatedDateTime(LocalDateTime.now())
                .setModifiedBy(employeeInfoService.getEmployee(newTravelApp.getTravelerEmpId()))
                .setModifiedDateTime(LocalDateTime.now())
                .setStatus(TravelApplicationStatus.SUBMITTED)
                .build();

        travelAppService.submitTravelApplication(app);
        return null;
    }

    @RequestMapping(value = "")
    public BaseResponse searchTravelApplications(@RequestParam int empId,
                                                 @RequestParam String status) {
        TravelApplicationStatus appStatus = TravelApplicationStatus.valueOf(status);
        List<TravelApplication> apps = travelAppService.searchTravelApplications(empId, appStatus);
        List<TravelApplicationView> appViews = travelApplicationsToViews(apps);
        return ListViewResponse.of(appViews);
    }

    private List<TravelApplicationView> travelApplicationsToViews(List<TravelApplication> apps) {
        return apps.stream()
                .map(TravelApplicationView::new)
                .collect(Collectors.toList());
    }
}
