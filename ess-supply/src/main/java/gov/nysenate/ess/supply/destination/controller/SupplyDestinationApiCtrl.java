package gov.nysenate.ess.supply.destination.controller;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.view.LocationView;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.dao.unit.LocationDao;
import gov.nysenate.ess.core.model.auth.SenatePerson;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supply/destinations")
public class SupplyDestinationApiCtrl extends BaseRestApiCtrl {

    @Autowired private LocationDao locationDao;
    @Autowired private EmployeeInfoService employeeService;

    /**
     * This API is used to get the list of locations an employee is
     * allowed to select as a destination for their order.
     *
     * Regular employees can only select destinations that
     * are part of their department.
     *
     * Supply employees are able to create orders for employees,
     * and therefore can select any destination.
     *
     * @param empId The employee who's valid destinations should
     *              be returned.
     */
    @RequestMapping(value = "/{empId}")
    public BaseResponse getDestinationsForEmployee(@PathVariable int empId) {
        return ListViewResponse.of(locationDao.getLocationsUnderResponsibilityHead(getLoggedInEmployee().getRespCenter().getHead())
                                              .stream()
                                              .map(LocationView::new)
                                              .collect(Collectors.toList()));
    }

    private Employee getLoggedInEmployee() {
        return employeeService.getEmployee(getSubjectEmployeeId());
    }

    private int getSubjectEmployeeId() {
        SenatePerson person = (SenatePerson) getSubject().getPrincipals().getPrimaryPrincipal();
        return person.getEmployeeId();
    }
}
