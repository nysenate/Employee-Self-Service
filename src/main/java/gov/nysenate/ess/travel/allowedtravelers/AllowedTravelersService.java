package gov.nysenate.ess.travel.allowedtravelers;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AllowedTravelersService {

    private SqlUserOrderableRchDao userOrderableRchDao;
    private EmployeeInfoService employeeInfoService;

    @Autowired
    public AllowedTravelersService(SqlUserOrderableRchDao userOrderableRchDao, EmployeeInfoService employeeInfoService) {
        this.userOrderableRchDao = userOrderableRchDao;
        this.employeeInfoService = employeeInfoService;
    }

    /**
     * See {@link #forEmp(Employee)}.
     *
     * @param empId
     * @return A set of Employees {@code empId} can place travel requests for.
     * @see #forEmp(Employee)
     */
    @NotNull
    public Set<Employee> forEmpId(int empId) {
        return forEmp(employeeInfoService.getEmployee(empId));
    }

    /**
     * Returns a set of employees the given {@code emp} is allowed to place travel requests for.
     * <p>
     * - Employees can place travel requests for all employees in their RCH (responsibility center head) by default.
     * - Some employees can submit travel requests for additional RCH's, See {@link SqlUserOrderableRchDao}.
     * - Senators are not allowed to travel, so they are removed from the returned set.
     *
     * @param emp An employee who is filling out a travel request.
     * @return A set of employees the given {@code emp} is allowed to place travel requests for. May return an empty
     * set if the employee is not assigned to an RCH.
     */
    @NotNull
    public Set<Employee> forEmp(@NotNull Employee emp) {
        Set<String> authorizedRchCodes = authorizedRchCodes(Objects.requireNonNull(emp));
        Set<Employee> activeEmployees = employeeInfoService.getAllEmployees(true);

        Set<Employee> allowedTravelers = filterEmployeesByRchCode(activeEmployees, authorizedRchCodes);
        allowedTravelers = removeSenators(allowedTravelers);
        return allowedTravelers;
    }

    /**
     * Filters {@code employees}, removing employees who's RCH Code is not in {@code authorizedRchCodes}.
     * Returns the filtered set.
     *
     * @param employees
     * @param authorizedRchCodes
     * @return A set containing zero or more {@link Employee Employee's}.
     */
    private Set<Employee> filterEmployeesByRchCode(Set<Employee> employees, Set<String> authorizedRchCodes) {
        return employees.stream()
                .filter(e -> authorizedRchCodes.contains(Optional.ofNullable(e.getRespCenterHeadCode()).orElse("")))
                .collect(Collectors.toSet());
    }

    // Senators are not allowed to travel, remove them from the allowed travelers list.
    private Set<Employee> removeSenators(Set<Employee> allowedTravelers) {
        return allowedTravelers.stream()
                .filter(e -> !e.isSenator())
                .collect(Collectors.toSet());
    }

    /**
     * @param emp
     * @return A set of RCH Codes which {@code emp} is authorized to submit travel requests for.
     */
    private Set<String> authorizedRchCodes(@NotNull Employee emp) {
        Set<String> authorizedRchs = new HashSet<>();

        // Add the employess own RCH if they are assigned one.
        if (emp.getRespCenterHeadCode() != null) {
            authorizedRchs.add(emp.getRespCenterHeadCode());
        }

        // Add any additional RCH's for the emp.
        authorizedRchs.addAll(userOrderableRchDao.forEmpId(emp.getEmployeeId()));
        return authorizedRchs;
    }
}
