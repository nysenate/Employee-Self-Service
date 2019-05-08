package gov.nysenate.ess.core.model.pec.acknowledgment;

import gov.nysenate.ess.core.model.personnel.Employee;

import java.util.ArrayList;
import java.util.List;

public class EmpAckReport {

    private Employee employee;
    private List<ReportAck> acks = new ArrayList<ReportAck>();

    /*
    Constructors
     */
    public EmpAckReport() {}

    //Report 1 we care about all emp
    public EmpAckReport(Employee employee) {
        this.employee = employee;
    }

    /*
    Getters and Setters
     */

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public List<ReportAck> getAcks() {
        return acks;
    }

    public void setAcks(List<ReportAck> acks) {
        this.acks = acks;
    }
}
