package gov.nysenate.ess.supply.destination.controller;

import com.google.common.collect.Lists;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.view.LocationView;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.supply.destination.DestinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supply/destinations")
public class SupplyDestinationApiCtrl extends BaseRestApiCtrl {

    @Autowired private EmployeeInfoService employeeService;
    @Autowired private DestinationService destinationService;

    /**
     * This API is used to get the list of locations an employee is
     * allowed to select as a destination for their order.
     * <p>
     * Regular employees can only select destinations that
     * are part of their department.
     * <p>
     * Supply employees are able to create orders for employees,
     * and therefore can select any destination.
     *
     * @param empId The employee who's valid destinations should
     *              be returned.
     */
    @RequestMapping(value = "/{empId}")
    public BaseResponse getDestinationsForEmployee(@PathVariable int empId) {
        Employee employee = employeeService.getEmployee(empId);
        List<Location> locations = Lists.newArrayList(destinationService.employeeDestinations(employee));
        locations.sort(Comparator.comparing(l -> l.getLocId().getCode()));

        return ListViewResponse.of(locations.stream()
                .map(LocationView::new)
                .collect(Collectors.toList()));
    }
}
