package gov.nysenate.ess.travel.department;

import com.google.common.collect.Sets;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TravelDepartmentService {

    private final EmployeeInfoService employeeInfoService;
    private Set<Employee> activeEmployees;

    @Autowired
    public TravelDepartmentService(EmployeeInfoService employeeInfoService) {
        this.employeeInfoService = employeeInfoService;
    }

    public Department departmentForEmployee(Employee employee) {
        TravelDepartmentAssigner departmentAssigner = new TravelDepartmentAssigner(
                getActiveEmployees(), deptHeadEmpIds());
        return departmentAssigner.getDepartment(employee);
    }

    private Set<Integer> deptHeadEmpIds() {
        return Sets.newHashSet(3925, 231, 7070, 12729, 12983, 11849, 1963, 7048, 7689, 12696, 7688,
                10512, 11087, 9471, 11092, 6946, 7689, 10594, 12944, 3596, 13522, 7885, 58
        );
    }

    private Set<Employee> getActiveEmployees() {
        if (activeEmployees == null || activeEmployees.size() == 0) {
            activeEmployees = employeeInfoService.getAllEmployees(true);
        }
        return activeEmployees;
    }
}
