package gov.nysenate.ess.core.client.view.base;

import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.personnel.Employee;

public class EmployeeSearchView implements ViewObject {

    protected int empId;
    protected String firstName;
    protected String lastName;
    protected String fullName;
    protected PayType payType;
    protected boolean isSenator;

    public EmployeeSearchView(Employee employee) {
        this.empId = employee.getEmployeeId();
        this.firstName = employee.getFirstName();
        this.lastName = employee.getLastName();
        this.fullName = employee.getFullName();
        this.payType = employee.getPayType();
        this.isSenator = employee.isSenator();
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
        return isSenator;
    }

    @Override
    public String getViewType() {
        return "employee-search-result";
    }
}
