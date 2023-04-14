package gov.nysenate.ess.travel.department;

import gov.nysenate.ess.core.model.personnel.Employee;
import org.apache.commons.lang3.ObjectUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TravelDepartmentAssigner {

    private final Set<Employee> activeEmployees;
    private final Set<Integer> departmentHeadIds;

    public TravelDepartmentAssigner(Set<Employee> activeEmployees, Set<Integer> departmentHeadIds) {
        this.activeEmployees = activeEmployees;
        this.departmentHeadIds = departmentHeadIds;
    }

    /**
     * Find and construct the Department that the given employee belongs to or is the head of.
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
        Employee senatorDeptHead = senatorOfficeDeptHead(employee);
        Employee supChainDeptHead = supChainDeptHead(employee);
        return ObjectUtils.firstNonNull(senatorDeptHead, supChainDeptHead);
    }

    private Employee senatorOfficeDeptHead(Employee employee) {
        if (employee.getRespCenter() == null) {
            return null;
        }
        int respCtrCode = employee.getRespCenter().getCode();
        return activeEmployees.stream()
                .filter(e -> e.getRespCenter() != null)
                .filter(e -> e.getRespCenter().getCode() == respCtrCode)
                .filter(Employee::isSenator)
                .findFirst()
                .orElse(null);
    }

    private Employee supChainDeptHead(Employee employee) {
        Map<Integer, Employee> idToEmp = activeEmployees.stream()
                .collect(Collectors.toMap(Employee::getEmployeeId, Function.identity()));
        var prev = employee;
        var curr = idToEmp.get(employee.getSupervisorId());
        while (curr.getSupervisorId() != prev.getEmployeeId()) {
            if (departmentHeadIds.contains(curr.getEmployeeId())) {
                return curr;
            }
            prev = curr;
            curr = idToEmp.get(curr.getSupervisorId());
        }
        return null;
    }

    private boolean isDepartmentHead(Employee employee) {
        return employee.isSenator() || departmentHeadIds.contains(employee.getEmployeeId());
    }
}
