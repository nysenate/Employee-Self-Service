package gov.nysenate.ess.travel.department;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.request.department.SqlDepartmentHeadDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TravelDepartmentService {

    private final EmployeeInfoService employeeInfoService;
    private final SqlDepartmentHeadDao departmentHeadDao;
    private Set<Employee> activeEmployees;

    @Autowired
    public TravelDepartmentService(EmployeeInfoService employeeInfoService, SqlDepartmentHeadDao departmentHeadDao) {
        this.employeeInfoService = employeeInfoService;
        this.departmentHeadDao = departmentHeadDao;
    }

    public Department departmentForEmployee(Employee employee) {
        TravelDepartmentAssigner departmentAssigner = new TravelDepartmentAssigner(
                getActiveEmployees(), departmentHeadDao.currentDeptHdIds());
        return departmentAssigner.getDepartment(employee);
    }

    private Set<Employee> getActiveEmployees() {
        if (activeEmployees == null || activeEmployees.size() == 0) {
            activeEmployees = employeeInfoService.getAllEmployees(true);
        }
        return activeEmployees;
    }
}
