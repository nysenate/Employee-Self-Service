package gov.nysenate.ess.travel.application;

import com.google.maps.errors.ApiException;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.view.DetailedEmployeeView;
import gov.nysenate.ess.core.client.view.base.ListView;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.auth.SenatePerson;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/application")
public class TravelApplicationCtrl extends BaseRestApiCtrl {

    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private TravelApplicationFactory applicationFactory;
    @Autowired private InMemoryTravelAppDao appDao;

    /**
     * Initialize a mostly empty travel app, containing just the traveling {@link Employee}
     * and the submitter {@link Employee}
     * <p>
     * (POST) /api/v1/travel/application/init/:id
     * <p>
     * Path Params: id (int) - The travelers employee id.
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/init/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse initTravelApplication(@PathVariable int id) {
        // TODO Check submitter's permission, are they allowed to submit a travel app for traveler?
        Employee traveler = employeeInfoService.getEmployee(id);
//        Employee submitter = employeeInfoService.getEmployee(getSubjectEmployeeId());
        TravelApplication app = new TravelApplication(0, traveler, traveler); // TODO better way to implement Id's?
        return new ViewObjectResponse<>(new TravelApplicationView(app));
    }

    /**
     * @param appView
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse saveTravelApplication(@RequestBody TravelApplicationView appView) throws InterruptedException, ApiException, IOException {
        TravelApplication app = applicationFactory.createApplication(appView);
        appDao.saveTravelApplication(app);
        return new ViewObjectResponse<>(new TravelApplicationView(app));
    }

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

    private int getSubjectEmployeeId() {
        SenatePerson person = (SenatePerson) getSubject().getPrincipals().getPrimaryPrincipal();
        return person.getEmployeeId();
    }

    @RequestMapping(value = "/test")
    public BaseResponse testListView() {
        return new ViewObjectResponse();
    }

    @RequestMapping(value = "/test/submit")
    public BaseResponse testListView(@RequestParam ListView list) {
        System.out.println("sdfsd");
        return null;
    }
}
