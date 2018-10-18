package gov.nysenate.ess.supply.employee;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.dao.security.authorization.RoleDao;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.supply.authorization.permission.SupplyPermission;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Set;

/**
 * An API for getting information about supply employees.
 */
@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supply/employees")
public class SupplyEmployeeApiCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(SupplyEmployeeApiCtrl.class);
    @Autowired private RoleDao roleDao;
    @Autowired private SupplyEmployeeDao supplyEmployeeDao;

    @RequestMapping("")
    public BaseResponse getSupplyEmployees() {
        checkPermission(SupplyPermission.SUPPLY_EMPLOYEE.getPermission());
        ImmutableList<Employee> employees = roleDao.getEmployeesWithRole(EssRole.SUPPLY_EMPLOYEE);
        return ListViewResponse.of(new ArrayList(employees));
    }

    /**
     * Returns all employees who have ever issued a requisition.
     */
    @RequestMapping("/issuers")
    public BaseResponse getIssuers() {
        checkPermission(SupplyPermission.SUPPLY_EMPLOYEE.getPermission());
        Set<Employee> employees = supplyEmployeeDao.getDistinctIssuers();
        return ListViewResponse.of(new ArrayList(employees));
    }
}
