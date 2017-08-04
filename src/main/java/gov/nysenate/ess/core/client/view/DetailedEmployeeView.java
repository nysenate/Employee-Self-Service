package gov.nysenate.ess.core.client.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.PersonnelStatus;
import gov.nysenate.ess.core.model.personnel.ResponsibilityCenter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DetailedEmployeeView extends EmployeeView
{
    // Keep the private fields to store some data from the Employee model in case we need to recreate it.
    @JsonIgnore private PayType empPayType;
    @JsonIgnore private ResponsibilityCenter empRespCtr;

    protected String nid;
    protected int supervisorId;
    protected String jobTitle;
    protected String payType;
    protected boolean senator;
    protected RespCenterView respCtr;
    protected AddressView workAddress;
    protected LocationView empWorkLocation;
    protected PersonnelStatus personnelStatus;

    public DetailedEmployeeView() {}

    public DetailedEmployeeView(Employee employee) {
        super(employee);
        this.nid = employee.getNid();
        this.supervisorId = employee.getSupervisorId();
        this.jobTitle = employee.getJobTitle();
        this.empPayType = employee.getPayType();
        this.payType = (employee.getPayType() != null) ? employee.getPayType().name() : "";
        this.senator = employee.isSenator();
        this.empRespCtr = employee.getRespCenter();
        this.respCtr = new RespCenterView(employee.getRespCenter());
        this.personnelStatus = employee.getPersonnelStatus();
        if (employee.getWorkLocation() != null) {
            this.empWorkLocation = new LocationView(employee.getWorkLocation());
            this.workAddress = new AddressView(employee.getWorkLocation().getAddress());
        }
    }

    @JsonIgnore
    public Employee toEmployee() {
        Employee emp = super.toEmployee();
        emp.setNid(nid);
        emp.setSupervisorId(supervisorId);
        emp.setJobTitle(jobTitle);
        emp.setRespCenter(empRespCtr);
        emp.setPayType(empPayType);
        emp.setWorkLocation(empWorkLocation.toLocation());
        return emp;
    }

    @Override
    @XmlElement
    public String getViewType() {
        return "employee-detail";
    }

    @XmlElement
    public String getNid() {
        return nid;
    }

    @XmlElement
    public int getSupervisorId() {
        return supervisorId;
    }

    @XmlElement
    public String getJobTitle() {
        return jobTitle;
    }

    @XmlElement
    public String getPayType() {
        return payType;
    }

    @XmlElement
    public RespCenterView getRespCtr() {
        return respCtr;
    }

    @XmlElement
    public AddressView getWorkAddress() {
        return workAddress;
    }

    @XmlElement
    public LocationView getEmpWorkLocation() {
        return empWorkLocation;
    }

    @XmlElement
    public boolean isSenator() {
        return senator;
    }

    @XmlElement
    public PersonnelStatus getPersonnelStatus() {
        return personnelStatus;
    }
}
