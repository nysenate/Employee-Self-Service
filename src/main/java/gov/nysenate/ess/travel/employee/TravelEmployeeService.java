package gov.nysenate.ess.travel.employee;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.department.TravelDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TravelEmployeeService {

    private TravelDepartmentService departmentService;
    private EmployeeInfoService employeeInfoService;

    @Autowired
    public TravelEmployeeService(TravelDepartmentService departmentService,
                                 EmployeeInfoService employeeInfoService) {
        this.departmentService = departmentService;
        this.employeeInfoService = employeeInfoService;
    }

    public TravelEmployee getTravelEmployee(Employee employee) {
        return new TravelEmployee(employee, departmentService.departmentForEmployee(employee));
    }


    public TravelEmployee getTravelEmployee(int employeeId) {
        return getTravelEmployee(employeeInfoService.getEmployee(employeeId));
    }

    public Set<TravelEmployee> getTravelEmployees(Set<Employee> employees) {
        return employees.stream()
                .map(this::getTravelEmployee)
                .collect(Collectors.toSet());
    }
}
