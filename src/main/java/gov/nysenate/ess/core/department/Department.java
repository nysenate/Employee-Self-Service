package gov.nysenate.ess.core.department;

import java.util.HashSet;
import java.util.Set;

public class Department {
    private int id;
    private String name;
    // The employee id of the department head.
    private int headEmpId;
    // A department gets set to inactive if no employees are assigned to it.
    private boolean isActive;
    // Employees who are in this department.
    private Set<Integer> employeeIds;

    public Department(String name) {
        this(0, name, 0, true);
    }

    public Department(String name, int headEmpId) {
        this(0, name, headEmpId, true);
    }

    public Department(String name, Set<Integer> employeeIds) {
        this(0, name, 0, true, employeeIds);
    }

    public Department(int departmentId, String name, int headEmpId, boolean isActive) {
        this(departmentId, name, headEmpId, isActive, new HashSet<>());
    }

    public Department(int departmentId, String name, int headEmpId, boolean isActive, Set<Integer> employeeIds) {
        this.id = departmentId;
        this.name = name;
        this.headEmpId = headEmpId;
        this.isActive = isActive;
        this.employeeIds = employeeIds;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getHeadEmpId() {
        return headEmpId;
    }

    public boolean isActive() {
        return isActive;
    }

    public Set<Integer> getEmployeeIds() {
        return employeeIds;
    }

    void setId(int id) {
        this.id = id;
    }

    void addEmployee(int empId) {
        this.employeeIds.add(empId);
    }

    void setEmployees(Set<Integer> employeeIds) {
        this.employeeIds = employeeIds;
    }

    void setActive(boolean active) {
        isActive = active;
    }
}
