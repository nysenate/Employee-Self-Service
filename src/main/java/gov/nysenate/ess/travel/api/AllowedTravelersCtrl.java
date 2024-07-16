package gov.nysenate.ess.travel.api;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.allowedtravelers.AllowedTravelersService;
import gov.nysenate.ess.travel.employee.TravelEmployee;
import gov.nysenate.ess.travel.employee.TravelEmployeeService;
import gov.nysenate.ess.travel.employee.TravelEmployeeView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/allowed-travelers")
public class AllowedTravelersCtrl extends BaseRestApiCtrl {

    @Autowired private AllowedTravelersService allowedTravelersService;
    @Autowired private TravelEmployeeService travelEmployeeService;

    @RequestMapping("")
    public BaseResponse fetchAllowedTravelers() {
        Set<Employee> allowedEmps = allowedTravelersService.forEmpId(getSubjectEmployeeId());
        Set<TravelEmployee> allowedTravelers = travelEmployeeService.getTravelEmployees(allowedEmps);
        return ListViewResponse.of(
                allowedTravelers.stream()
                        .map(TravelEmployeeView::new)
                        .collect(Collectors.toList()));
    }
}

