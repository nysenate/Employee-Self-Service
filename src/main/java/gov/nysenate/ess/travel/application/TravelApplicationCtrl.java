package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel")
public class TravelApplicationCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(TravelApplicationCtrl.class);
    @Autowired private TravelApplicationService travelApplicationService;
    @Autowired private EmployeeInfoService employeeInfoService;

    @RequestMapping(value = "/application/{appId}", method = RequestMethod.GET)
    public BaseResponse getTravelAppById(@PathVariable int appId) {
        TravelApplication app = travelApplicationService.getTravelApplication(appId);
        checkTravelAppPermission(app, RequestMethod.GET);
        return new ViewObjectResponse<>(new TravelApplicationView(app));
    }

    @RequestMapping(value = "/application/{appId}", method = RequestMethod.POST)
    public void saveTravelApp(@PathVariable int appId, @RequestBody TravelApplicationView appView) {
        TravelApplication app = travelApplicationService.getTravelApplication(appId);
        checkTravelAppPermission(app, RequestMethod.POST);

        Employee user = employeeInfoService.getEmployee(getSubjectEmployeeId());
        travelApplicationService.saveTravelApplication(appView.toTravelApplication(), user);
    }

    @RequestMapping(value = "/applications")
    public BaseResponse getActiveTravelApps() {
        List<TravelApplication> apps = travelApplicationService.selectTravelApplications(getSubjectEmployeeId());
        List<TravelApplicationView> appViews = apps.stream()
                .map(TravelApplicationView::new)
                .collect(Collectors.toList());
        return ListViewResponse.of(appViews);
    }
}
