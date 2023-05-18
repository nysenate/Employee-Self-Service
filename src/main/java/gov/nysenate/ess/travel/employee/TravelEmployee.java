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
        return this.employeeId == getDeptHeadId();
    }

    public Department getDepartment() {
        return this.department;
    }

    /**
     * Returns the employees department head employee id if they have one, 0 otherwise.
     */
    public int getDeptHeadId() {
        if (department == null || department.getHead() == null) {
            return 0;
        }
        return department.getHead().getEmployeeId();
    }
}
