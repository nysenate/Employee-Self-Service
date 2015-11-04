package gov.nysenate.ess.web.model.payroll;

import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.personnel.Employee;

import java.math.BigDecimal;
import java.util.Date;

public class EmployeePayInfo {
    protected Employee employee;
    protected PayType payType;
    protected BigDecimal salary;
    protected Date payUpdatedDate;
    protected Date w4SignedDate;
    protected boolean directDepositActive;

    public EmployeePayInfo(Employee employee) {
        this.employee = employee;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public PayType getPaytype() {
        return payType;
    }

    public void setPaytype(PayType payType) {
        this.payType = payType;
    }

    public Date getPayUpdatedDate() {
        return payUpdatedDate;
    }

    public void setPayUpdatedDate(Date payUpdatedDate) {
        this.payUpdatedDate = payUpdatedDate;
    }

    public Date getW4SignedDate() {
        return w4SignedDate;
    }

    public void setW4SignedDate(Date w4SignedDate) {
        this.w4SignedDate = w4SignedDate;
    }

    public boolean isDirectDepositActive() {
        return directDepositActive;
    }

    public void setDirectDepositActive(boolean directDepositActive) {
        this.directDepositActive = directDepositActive;
    }
}