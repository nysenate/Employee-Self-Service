package gov.nysenate.ess.core.department;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.List;
import java.util.Set;

public class DepartmentView implements ViewObject {

    private int id;
    private String name;
    private int headEmpId;
    private boolean isActive;
    private Set<Integer> employeeIds;

    public DepartmentView() {
    }

    public DepartmentView(Department department) {
        if (department != null) {
            this.id = department.getId();
            this.name = department.getName();
            this.headEmpId = department.getHeadEmpId();
            this.isActive = department.isActive();
            this.employeeIds = department.getEmployeeIds();
        }
    }

    public Department toDepartment() {
        LdapDepartment ldapDepartment = new LdapDepartment(this.name, this.employeeIds);
        return new Department(this.id, ldapDepartment, this.headEmpId, this.isActive);
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

    @JsonProperty("isActive")
    public boolean isActive() {
        return isActive;
    }

    public Set<Integer> getEmployeeIds() {
        return employeeIds;
    }

    @Override
    public String getViewType() {
        return "department";
    }
}
