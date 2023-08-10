package gov.nysenate.ess.travel.department;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.request.department.SqlDepartmentHeadDao;
import gov.nysenate.ess.travel.request.department.SqlDepartmentHeadOverridesDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TravelDepartmentService {

    private final EmployeeInfoService employeeInfoService;
    private final SqlDepartmentHeadDao departmentHeadDao;
    private final SqlDepartmentHeadOverridesDao departmentHeadOverridesDao;
    private Set<Employee> activeEmployees;

    @Autowired
    public TravelDepartmentService(EmployeeInfoService employeeInfoService, SqlDepartmentHeadDao departmentHeadDao,
                                   SqlDepartmentHeadOverridesDao departmentHeadOverridesDao) {
        this.employeeInfoService = employeeInfoService;
        this.departmentHeadDao = departmentHeadDao;
        this.departmentHeadOverridesDao = departmentHeadOverridesDao;
    }

    public Department departmentForEmployee(Employee employee) {
        TravelDepartmentAssigner departmentAssigner = new TravelDepartmentAssigner(
                getActiveEmployees(),
                allDepartmentHeadIds(),
                departmentHeadOverridesDao.currentOverrides()
        );
        return departmentAssigner.getDepartment(employee);
    }

    // TODO this needs to refresh occasionally....
    private Set<Employee> getActiveEmployees() {
        if (activeEmployees == null || activeEmployees.size() == 0) {
            activeEmployees = employeeInfoService.getAllEmployees(true);
        }
        return activeEmployees;
    }

    /**
     * All department heads ids from admin offices or defined in the overrides table.
     * Does not include senators unless they are in the overrides table.
     */
    private Set<Integer> allDepartmentHeadIds() {
        Set<Integer> deptHeadIds = departmentHeadDao.currentDeptHdIds();
        deptHeadIds.addAll(departmentHeadOverridesDao.currentDepartmentHeads());
        return deptHeadIds;
    }
}
