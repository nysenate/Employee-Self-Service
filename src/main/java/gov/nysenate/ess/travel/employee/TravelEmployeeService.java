package gov.nysenate.ess.travel.employee;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.department.DepartmentNotFoundEx;
import gov.nysenate.ess.travel.department.TravelDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

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

    public TravelEmployee loadTravelEmployee(Employee employee) throws DepartmentNotFoundEx {
        return new TravelEmployee(employee, departmentService.departmentForEmployee(employee));
    }

    public TravelEmployee loadTravelEmployee(int employeeId) throws DepartmentNotFoundEx {
        return loadTravelEmployee(employeeInfoService.getEmployee(employeeId));
    }

    /**
     * Loads travel employees for each provided employee.
     * Does not throw a {@link DepartmentNotFoundEx} if an employee is missing a department. Instead, that employee
     * will not be included in the results.
     */
    public Set<TravelEmployee> loadTravelEmployeesSafe(Set<Employee> employees) {
        Set<TravelEmployee> travelEmployees = new HashSet<>();
        for (Employee employee : employees) {
            try {
                travelEmployees.add(loadTravelEmployee(employee));
            } catch (DepartmentNotFoundEx ignored) {
                // This employee will not be included in the returned set.
            }
        }
        return travelEmployees;
    }
}
