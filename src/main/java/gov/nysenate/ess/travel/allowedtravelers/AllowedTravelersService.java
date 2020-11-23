package gov.nysenate.ess.travel.allowedtravelers;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AllowedTravelersService {

    @Autowired private SqlUserOrderableRchDao userOrderableRchDao;
    @Autowired private EmployeeInfoService employeeInfoService;

    public Set<Employee> forEmpId(int empId) {
        return forEmp(employeeInfoService.getEmployee(empId));
    }

    /**
     * Returns all employees the given emp is allowed to place travel applications for.
     *
     * All users can place orders for users in their own RCH by default.
     * Some users can place orders for additional RCH's.
     * @param emp
     * @return
     */
    public Set<Employee> forEmp(Employee emp) {
        Set<String> allowedRchs = allowedRchs(emp);
        Set<Employee> allEmployees = employeeInfoService.getAllEmployees(true);
        Set<Employee> allowedTravelers = empsInRchs(allEmployees, allowedRchs);
        return allowedTravelers;
    }

    /**
     * Returns a set of all employees who have an RCH in allowedRchs.
     * @param allEmployees
     * @param allowedRchs
     * @return
     */
    private Set<Employee> empsInRchs(Set<Employee> allEmployees, Set<String> allowedRchs) {
        return allEmployees.stream()
                .filter(e -> allowedRchs.contains(Optional.ofNullable(e.getRespCenterHeadCode()).orElse("")))
                .collect(Collectors.toSet());
    }

    private Set<String> allowedRchs(Employee emp) {
        Set<String> allowedRchs = new HashSet<>();

        // Add the employess own RCH if its set.
        if (emp.getRespCenterHeadCode() != null) {
            allowedRchs.add(emp.getRespCenterHeadCode());
        }

        // Add any additional RCH's for the emp.
        allowedRchs.addAll(userOrderableRchDao.forEmpId(emp.getEmployeeId()));
        return allowedRchs;
    }
}
