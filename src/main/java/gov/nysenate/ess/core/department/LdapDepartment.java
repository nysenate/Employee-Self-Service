package gov.nysenate.ess.core.department;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import gov.nysenate.ess.core.model.auth.SenateLdapPerson;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class LdapDepartment {

    // The name of the department
    private final String name;
    // Employees who are in this department.
    private final ImmutableSet<Integer> employeeIds;

    public LdapDepartment(String name) {
        this(name, new HashSet<>());
    }

    public LdapDepartment(String name, Collection<Integer> employeeIds) {
        this.name = name;
        this.employeeIds = ImmutableSet.copyOf(employeeIds);
    }

    public String getName() {
        return name;
    }

    public ImmutableSet<Integer> getEmployeeIds() {
        return employeeIds;
    }

    @Override
    public String toString() {
        return "LdapDepartment{" +
                "name='" + name + '\'' +
                ", employeeIds=" + employeeIds +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LdapDepartment that = (LdapDepartment) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(employeeIds, that.employeeIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, employeeIds);
    }
}
