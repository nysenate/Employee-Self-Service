package gov.nysenate.ess.travel.department;

import gov.nysenate.ess.core.model.personnel.Employee;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TravelDepartmentAssigner {

    private final Set<Employee> activeEmployees;
    private final Set<Integer> departmentHeadIds;
    private final Map<Integer, Integer> deptHdOverrides; // map of employee id to department head emp id.

    public TravelDepartmentAssigner(Set<Employee> activeEmployees, Set<Integer> departmentHeadIds,
                                    Map<Integer, Integer> deptHdOverrides) {
        this.activeEmployees = activeEmployees;
        this.departmentHeadIds = departmentHeadIds;
        this.deptHdOverrides = deptHdOverrides;
    }

    /**
     * Find and construct the Department that the given employee belongs to or is the head of.
     *
     * @param employee
     * @return A Department or null if the department could not be determined.
     */
    public Department getDepartment(Employee employee) {
        Employee departmentHead = getDepartmentHead(employee);
        if (departmentHead == null) {
            return null;
        }
        Set<Employee> subordinates = getSubordinates(departmentHead);
        return new Department(departmentHead, subordinates);
    }

    private Set<Employee> getSubordinates(Employee departmentHead) {
        Set<Employee> subordinates = new HashSet<>();
        for (var emp : activeEmployees) {
            Employee deptHead = getDepartmentHead(emp);
            if (deptHead != null && deptHead.getEmployeeId() == departmentHead.getEmployeeId()) {
                subordinates.add(emp);
            }
        }
        return subordinates;
    }

    private Employee getDepartmentHead(Employee employee) {
        if (isDepartmentHead(employee)) {
            return employee;
        }
        return Stream.of(overrideDeptHead(employee), senatorOfficeDeptHead(employee), supChainDeptHead(employee))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElse(null);
    }

    private Optional<Employee> overrideDeptHead(Employee employee) {
        Integer deptHeadId = deptHdOverrides.get(employee.getEmployeeId());
        if (deptHeadId == null) {
            return Optional.empty();
        }
        return activeEmployees.stream()
                .filter(e -> e.getEmployeeId() == deptHeadId)
                .findFirst();
    }

    /**
     * Finds the department head for employees working under senators.
     *
     * @param employee
     * @return
     */
    private Optional<Employee> senatorOfficeDeptHead(Employee employee) {
        if (employee.getRespCenter() == null) {
            return Optional.empty();
        }
        int respCtrCode = employee.getRespCenter().getCode();
        return activeEmployees.stream()
                .filter(e -> e.getRespCenter() != null)
                .filter(e -> e.getRespCenter().getCode() == respCtrCode)
                .filter(Employee::isSenator)
                .findFirst();
    }

    /**
     * Finds the department head for employees working in admin offices.
     *
     * @param employee
     * @return
     */
    private Optional<Employee> supChainDeptHead(Employee employee) {
        Map<Integer, Employee> idToEmp = activeEmployees.stream()
                .collect(Collectors.toMap(Employee::getEmployeeId, Function.identity()));
        var prev = employee;
        var curr = idToEmp.get(employee.getSupervisorId());
        while (curr != null && curr.getSupervisorId() != prev.getEmployeeId()) {
            if (departmentHeadIds.contains(curr.getEmployeeId())) {
                return Optional.of(curr);
            }
            prev = curr;
            curr = idToEmp.get(curr.getSupervisorId());
        }
        return Optional.empty();
    }

    private boolean isDepartmentHead(Employee employee) {
        return employee.isSenator() || departmentHeadIds.contains(employee.getEmployeeId());
    }
}
