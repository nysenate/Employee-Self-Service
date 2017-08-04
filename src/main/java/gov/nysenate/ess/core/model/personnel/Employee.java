package gov.nysenate.ess.core.model.personnel;

import com.google.common.base.Objects;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.unit.Location;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Represents information that identifies an employee in the NYS Senate
 */
public class Employee extends Person
{

    protected int employeeId;
    protected int supervisorId;
    protected boolean active;
    protected PersonnelStatus personnelStatus;
    protected String uid;
    protected String jobTitle;
    protected PayType payType;
    protected String nid;
    protected ResponsibilityCenter respCenter;
    protected Location workLocation;
    protected LocalDateTime updateDateTime;
    protected LocalDate senateContServiceDate;

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
        this.respCenter = Optional.ofNullable(other.respCenter)
                .map(ResponsibilityCenter::new)
                .orElse(null);
        this.workLocation = other.workLocation;
        this.updateDateTime = other.updateDateTime;
    }

    /* --- Functional Getters --- */

    public boolean isSenator() {
        return Optional.ofNullable(respCenter)
                .map(ResponsibilityCenter::getAgency)
                .map(Agency::isSenator)
                .orElse(false);
    }

    /* --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee employee = (Employee) o;
        return employeeId == employee.employeeId &&
                supervisorId == employee.supervisorId &&
                active == employee.active &&
                personnelStatus == employee.personnelStatus &&
                Objects.equal(uid, employee.uid) &&
                Objects.equal(jobTitle, employee.jobTitle) &&
                payType == employee.payType &&
                Objects.equal(nid, employee.nid) &&
                Objects.equal(respCenter, employee.respCenter) &&
                Objects.equal(workLocation, employee.workLocation) &&
                Objects.equal(updateDateTime, employee.updateDateTime) &&
                Objects.equal(senateContServiceDate, employee.senateContServiceDate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(employeeId, supervisorId, active, personnelStatus, uid, jobTitle, payType, nid,
                respCenter, workLocation, updateDateTime, senateContServiceDate);
    }

    /* --- Functional Getters/Setters --- */

    /**
     * Convenience method that retrieves the resp ctr head code
     * from the {@link ResponsibilityCenter} contained in this object
     */
    public String getRespCenterHeadCode() {
        return Optional.ofNullable(this.getRespCenter())
                .map(ResponsibilityCenter::getHead)
                .map(ResponsibilityHead::getCode)
                .orElse(null);
    }

    /* --- Basic Getters/Setters --- */

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

    public PersonnelStatus getPersonnelStatus() {
        return personnelStatus;
    }

    public void setPersonnelStatus(PersonnelStatus personnelStatus) {
        this.personnelStatus = personnelStatus;
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

    public LocalDate getSenateContServiceDate() {
        return senateContServiceDate;
    }

    public void setSenateContServiceDate(LocalDate senateContServiceDate) {
        this.senateContServiceDate = senateContServiceDate;
    }
}