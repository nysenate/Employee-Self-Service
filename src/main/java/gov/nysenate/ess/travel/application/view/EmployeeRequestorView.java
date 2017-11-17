package gov.nysenate.ess.travel.application.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.model.EmployeeRequestorInfo;

import java.util.Date;

public class EmployeeRequestorView implements ViewObject{

    private int empId;
    private int requestorId;
    private Date startDate;
    private Date endDate;

    public EmployeeRequestorView(EmployeeRequestorInfo employeeRequestorInfo){
        this.empId = employeeRequestorInfo.getEmpId();
        this.requestorId = employeeRequestorInfo.getRequestorId();
        this.startDate = employeeRequestorInfo.getStartDate();
        this.endDate = employeeRequestorInfo.getEndDate();
    }

    @Override
    public String getViewType() {
        return "employee-requestor";
    }
}
