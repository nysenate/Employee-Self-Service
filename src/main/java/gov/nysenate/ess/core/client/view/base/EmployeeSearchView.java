package gov.nysenate.ess.core.client.view.base;

import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.personnel.Employee;

public class EmployeeSearchView implements ViewObject {

    protected int empId;
    protected String firstName;
    protected String lastName;
    protected String fullName;
    protected PayType payType;
    protected boolean senator;
    protected boolean active;

    public EmployeeSearchView() {
    }

    public EmployeeSearchView(Employee employee) {
        this.empId = employee.getEmployeeId();
        this.firstName = employee.getFirstName();
        this.lastName = employee.getLastName();
        this.fullName = employee.getFullName();
        this.payType = employee.getPayType();
        this.senator = employee.isSenator();
        this.active = employee.isActive();
    }

    public int getEmpId() {
        return empId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public PayType getPayType() {
        return payType;
    }

    public boolean isSenator() {
        return senator;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public String getViewType() {
        return "employee-search-result";
    }
}
