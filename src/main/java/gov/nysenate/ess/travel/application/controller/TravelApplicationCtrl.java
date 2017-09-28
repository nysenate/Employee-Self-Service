package gov.nysenate.ess.travel.application.controller;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.travel.application.model.TravelApplication;
import gov.nysenate.ess.travel.application.model.TravelApplicationStatus;
import gov.nysenate.ess.travel.application.service.TravelApplicationService;
import gov.nysenate.ess.travel.application.view.TravelApplicationView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/travel-application")
public class TravelApplicationCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(TravelApplicationCtrl.class);

    @Autowired private TravelApplicationService travelAppService;

    @RequestMapping(value = "")
    public BaseResponse searchTravelApplications(@RequestParam int empId,
                                                 @RequestParam String status) {
        TravelApplicationStatus appStatus = TravelApplicationStatus.valueOf(status);
        List<TravelApplication> apps = travelAppService.searchTravelApplications(empId, appStatus);
        List<TravelApplicationView> appViews = apps.stream()
                .map(TravelApplicationView::new)
                .collect(Collectors.toList());
        return ListViewResponse.of(appViews);
    }
}
