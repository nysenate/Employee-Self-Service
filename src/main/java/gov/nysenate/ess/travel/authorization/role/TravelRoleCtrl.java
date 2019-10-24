package gov.nysenate.ess.travel.authorization.role;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/roles")
public class TravelRoleCtrl extends BaseRestApiCtrl {

    @Autowired private TravelRoleFactory roleFactory;
    @Autowired private EmployeeInfoService employeeInfoService;

    @RequestMapping("/{empId}")
    public BaseResponse getEmployeeTravelRoles(@PathVariable int empId) {
        Employee emp = employeeInfoService.getEmployee(empId);
        TravelRoles roles = roleFactory.travelRolesForEmp(emp);
        return new ViewObjectResponse<>(new TravelRolesView(roles));
    }
}
