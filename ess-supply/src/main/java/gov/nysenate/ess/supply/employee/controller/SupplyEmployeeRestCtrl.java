package gov.nysenate.ess.supply.employee.controller;

import com.google.common.collect.ImmutableCollection;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.supply.employee.service.SupplyEmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supply/employees")
public class SupplyEmployeeRestCtrl {

    private static final Logger logger = LoggerFactory.getLogger(SupplyEmployeeRestCtrl.class);

    @Autowired private SupplyEmployeeService supplyEmployeeService;

    @RequestMapping("")
    public BaseResponse getSupplyEmployees() {
        ImmutableCollection<Employee> suppplyEmps = supplyEmployeeService.getSupplyEmployees();
        List<EmployeeView> supplyEmpViews = suppplyEmps.stream()
                .map(EmployeeView::new)
                .collect(Collectors.toList());
        return ListViewResponse.of(supplyEmpViews);
    }
}
