package gov.nysenate.ess.travel.department;

import gov.nysenate.ess.core.model.personnel.Employee;

import java.util.Set;

public class Department {

    // The head of the Department.
    private Employee head;
    // All employees in the department besides the department head.
    private Set<Employee> subordinates;

    public Department(Employee head, Set<Employee> subordinates) {
        this.head = head;
        this.subordinates = subordinates;
    }

    public Employee getHead() {
        return head;
    }

    public Set<Employee> getSubordinates() {
        return subordinates;
    }
}
