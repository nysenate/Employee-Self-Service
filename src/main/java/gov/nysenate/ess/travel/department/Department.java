package gov.nysenate.ess.travel.department;

import gov.nysenate.ess.core.model.personnel.Employee;

import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return Objects.equals(head, that.head) && Objects.equals(subordinates, that.subordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(head, subordinates);
    }
}
