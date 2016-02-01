package gov.nysenate.ess.core.model.personnel;

import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.unit.Location;

import java.time.LocalDateTime;

/**
 * Represents information that identifies an employee in the NYS Senate
 */
public class Employee extends Person
{
    protected int employeeId;
    protected int supervisorId;
    protected boolean active;
    protected String uid;
    protected String jobTitle;
    protected PayType payType;
    protected String nid;
    protected ResponsibilityCenter respCenter;
    protected Location workLocation;
    protected LocalDateTime updateDateTime;

    public Employee() {}

    public Employee(Person person) {
        super(person);
    }

    public Employee(Employee other) {
        super(other);
        this.employeeId = other.employeeId;
        this.supervisorId = other.supervisorId;
        this.active = other.active;
        this.uid = other.uid;
        this.jobTitle = other.jobTitle;
        this.payType = other.payType;
        this.nid = other.nid;
        this.respCenter = other.respCenter;
        this.workLocation = other.workLocation;
        this.updateDateTime = other.updateDateTime;
    }

    /** --- Functional Getters --- */

    public boolean isSenator() {
        return respCenter != null && respCenter.getAgency() != null && "04210".equals(respCenter.getAgency().getCode());
    }

    /** --- Basic Getters/Setters --- */

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(int supervisorId) {
        this.supervisorId = supervisorId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public PayType getPayType() {
        return payType;
    }

    public void setPayType(PayType payType) {
        this.payType = payType;
    }

    public ResponsibilityCenter getRespCenter() {
        return respCenter;
    }

    public void setRespCenter(ResponsibilityCenter respCenter) {
        this.respCenter = respCenter;
    }

    public Location getWorkLocation() {
        return workLocation;
    }

    public void setWorkLocation(Location workLocation) {
        this.workLocation = workLocation;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(LocalDateTime updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Employee employee = (Employee) o;

        if (employeeId != employee.employeeId) return false;
        if (supervisorId != employee.supervisorId) return false;
        if (active != employee.active) return false;
        if (uid != null ? !uid.equals(employee.uid) : employee.uid != null) return false;
        if (jobTitle != null ? !jobTitle.equals(employee.jobTitle) : employee.jobTitle != null) return false;
        if (payType != employee.payType) return false;
        if (nid != null ? !nid.equals(employee.nid) : employee.nid != null) return false;
        if (respCenter != null ? !respCenter.equals(employee.respCenter) : employee.respCenter != null) return false;
        if (workLocation != null ? !workLocation.equals(employee.workLocation) : employee.workLocation != null)
            return false;
        return !(updateDateTime != null ? !updateDateTime.equals(employee.updateDateTime) : employee.updateDateTime != null);

    }

    @Override
    public int hashCode() {
        int result = employeeId;
        result = 31 * result + supervisorId;
        result = 31 * result + (active ? 1 : 0);
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        result = 31 * result + (jobTitle != null ? jobTitle.hashCode() : 0);
        result = 31 * result + (payType != null ? payType.hashCode() : 0);
        result = 31 * result + (nid != null ? nid.hashCode() : 0);
        result = 31 * result + (respCenter != null ? respCenter.hashCode() : 0);
        result = 31 * result + (workLocation != null ? workLocation.hashCode() : 0);
        result = 31 * result + (updateDateTime != null ? updateDateTime.hashCode() : 0);
        return result;
    }
}