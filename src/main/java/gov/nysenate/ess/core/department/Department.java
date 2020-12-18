package gov.nysenate.ess.core.department;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Department {
    private final int id;
    private final LdapDepartment ldapDepartment;
    // The employee id of the department head.
    private final int headEmpId;
    // A department gets set to inactive if no employees are assigned to it.
    private final boolean isActive;

    public Department(int id, int headEmpId, boolean isActive) {
        this(id, null, headEmpId, isActive);
    }

    public Department(LdapDepartment ldapDepartment) {
        this(0, ldapDepartment, 0, true);
    }

    public Department(int id, LdapDepartment ldapDepartment, int headEmpId, boolean isActive) {
        this.id = id;
        this.ldapDepartment = ldapDepartment;
        this.headEmpId = headEmpId;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public LdapDepartment getLdapDepartment() {
        return ldapDepartment;
    }

    public String getName() {
        return getLdapDepartment().getName();
    }

    public int getHeadEmpId() {
        return headEmpId;
    }

    public boolean isActive() {
        return isActive;
    }

    public Set<Integer> getEmployeeIds() {
        return getLdapDepartment().getEmployeeIds();
    }

    Department setId(int id) {
        return new Department(id, this.ldapDepartment, this.headEmpId, this.isActive);
    }

    Department setHeadEmpId(int headEmpId) {
        return new Department(id, this.ldapDepartment, headEmpId, this.isActive);
    }

    Department setLdapDepartment(LdapDepartment ldapDepartment) {
        return new Department(this.id, ldapDepartment, this.headEmpId, this.isActive);
    }

    Department setActive(boolean active) {
        return new Department(this.id, this.ldapDepartment, this.headEmpId, active);
    }

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", ldapDepartment=" + ldapDepartment +
                ", headEmpId=" + headEmpId +
                ", isActive=" + isActive +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return headEmpId == that.headEmpId &&
                isActive == that.isActive &&
                Objects.equals(ldapDepartment, that.ldapDepartment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ldapDepartment, headEmpId, isActive);
    }
}
