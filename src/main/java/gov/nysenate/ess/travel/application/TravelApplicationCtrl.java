package gov.nysenate.ess.travel.application;

import com.google.maps.errors.ApiException;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.auth.SenatePerson;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.supply.util.date.DateTimeFactory;
import gov.nysenate.ess.travel.utils.UploadProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/application")
public class TravelApplicationCtrl extends BaseRestApiCtrl {

    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private TravelApplicationFactory applicationFactory;
    @Autowired private InMemoryTravelAppDao appDao;

    // TODO Temporary for testing
    @RequestMapping(value = "")
    public BaseResponse getTravelApps(@RequestParam(required = false) String empId,
                                      @RequestParam(required = false) String id,
                                      @RequestParam(required = false, defaultValue = "false") boolean detailed) {
        if (empId != null) {
            List<TravelApplication> apps = appDao.getTravelAppsByTravelerId(Integer.valueOf(empId));
            List<TravelApplicationView> appViews = apps.stream()
                    .map(a -> detailed ? new DetailedTravelApplicationView(a) : new TravelApplicationView(a))
                    .collect(Collectors.toList());
            return ListViewResponse.of(appViews);
        }

        if (id != null) {
            TravelApplication app = appDao.getTravelAppById(Integer.valueOf(id));
            return new ViewObjectResponse<>(detailed ? new DetailedTravelApplicationView(app) : new TravelApplicationView(app));
        }
        return null;
    }

}
