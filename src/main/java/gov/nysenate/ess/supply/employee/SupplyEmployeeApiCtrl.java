package gov.nysenate.ess.supply.employee;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.dao.security.authorization.RoleDao;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.supply.authorization.permission.SupplyPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * An API for getting information about supply employees.
 */
@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supply/employees")
public class SupplyEmployeeApiCtrl extends BaseRestApiCtrl {
    private final RoleDao roleDao;
    private final SupplyEmployeeDao supplyEmployeeDao;

    @Autowired
    public SupplyEmployeeApiCtrl(RoleDao roleDao, SupplyEmployeeDao supplyEmployeeDao) {
        this.roleDao = roleDao;
        this.supplyEmployeeDao = supplyEmployeeDao;
    }

    @RequestMapping("")
    public BaseResponse getSupplyEmployees() {
        checkPermission(SupplyPermission.SUPPLY_STAFF_VIEW.getPermission());
        ImmutableList<Employee> employees = roleDao.getEmployeesWithRole(EssRole.SUPPLY_EMPLOYEE);
        return ListViewResponse.of(employees.stream().map(EmployeeView::new).toList());
    }

    /**
     * Returns all employees who have ever issued a requisition.
     */
    @RequestMapping("/issuers")
    public BaseResponse getIssuers() {
        checkPermission(SupplyPermission.SUPPLY_STAFF_VIEW.getPermission());
        Set<Employee> employees = supplyEmployeeDao.getDistinctIssuers();
        return ListViewResponse.of(employees.stream().map(EmployeeView::new).toList());
    }
}
