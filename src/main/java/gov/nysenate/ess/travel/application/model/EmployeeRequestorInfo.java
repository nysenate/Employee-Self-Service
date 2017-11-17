package gov.nysenate.ess.travel.application.model;

import java.util.Date;

public class EmployeeRequestorInfo {

    private int empId;
    private int requestorId;
    private Date startDate;
    private Date endDate;

    public EmployeeRequestorInfo(int empId, int requestorId, Date startDate, Date endDate) {
        this.empId = empId;
        this.requestorId = requestorId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getEmpId() {
        return empId;
    }

    public int getRequestorId() {
        return requestorId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }
}
