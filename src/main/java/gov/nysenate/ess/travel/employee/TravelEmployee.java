package gov.nysenate.ess.travel.employee;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.department.Department;

public class TravelEmployee extends Employee {

    private Department department;

    public TravelEmployee(Employee other, Department department) {
        super(other);
        this.department = department;
    }

    public boolean isDepartmentHead() {
        return this.employeeId == department.getHead().getEmployeeId();
    }

    public Department getDepartment() {
        return this.department;
    }
}
