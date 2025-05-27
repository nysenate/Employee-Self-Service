package gov.nysenate.ess.travel.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.client.view.LocationView;
import gov.nysenate.ess.core.client.view.RespCenterView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.department.DepartmentView;

public class TravelEmployeeView extends EmployeeView implements ViewObject {

    @JsonProperty("isDepartmentHead")
    private boolean isDepartmentHead;
    private DepartmentView department;
    private LocationView empWorkLocation;
    private String nid;
    private RespCenterView respCtr;
    private String jobTitle;

    public TravelEmployeeView() {
        super();
    }

    public TravelEmployeeView(TravelEmployee travelEmployee) {
        super(travelEmployee);
        this.isDepartmentHead = travelEmployee.isDepartmentHead();
        this.department = new DepartmentView(travelEmployee.getDepartment());
        this.empWorkLocation = travelEmployee.getWorkLocation() == null
                ? null
                : new LocationView(travelEmployee.getWorkLocation());
        this.nid = travelEmployee.getNid();
        this.respCtr = new RespCenterView(travelEmployee.getRespCenter());
        this.jobTitle = travelEmployee.getJobTitle();
    }

    public TravelEmployee toTravelEmployee() {
        Employee emp = toEmployee();
        emp.setNid(nid);
        emp.setWorkLocation(empWorkLocation.toLocation());
        emp.setRespCenter(respCtr.toResponsibilityCenter());
        emp.setJobTitle(jobTitle);
        return new TravelEmployee(emp, department.toDepartment());
    }

    @JsonIgnore
    public boolean isDepartmentHead() {
        return isDepartmentHead;
    }

    public DepartmentView getDepartment() {
        return department;
    }

    public void setDepartmentHead(boolean departmentHead) {
        isDepartmentHead = departmentHead;
    }

    public void setDepartment(DepartmentView department) {
        this.department = department;
    }

    public LocationView getEmpWorkLocation() {
        return empWorkLocation;
    }

    public void setEmpWorkLocation(LocationView empWorkLocation) {
        this.empWorkLocation = empWorkLocation;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public RespCenterView getRespCtr() {
        return respCtr;
    }

    public void setRespCtr(RespCenterView respCtr) {
        this.respCtr = respCtr;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    @Override
    public String getViewType() {
        return "travel-employee";
    }
}
