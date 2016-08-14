package gov.nysenate.ess.time.client.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.time.model.payroll.MiscLeaveGrant;
import gov.nysenate.ess.time.model.payroll.MiscLeaveType;

import javax.xml.bind.annotation.XmlElement;
import java.time.LocalDate;

public class MiscLeaveGrantView implements ViewObject {

    private int empId;
    private MiscLeaveType miscLeaveType;
    private LocalDate beginDate;
    private LocalDate endDate;

    public MiscLeaveGrantView(MiscLeaveGrant grant) {
        this.empId = grant.getEmpId();
        this.miscLeaveType = grant.getMiscLeaveType();
        this.beginDate = grant.getBeginDate();
        this.endDate = grant.getEndDate();
    }

    @XmlElement
    public int getEmpId() {
        return empId;
    }

    @XmlElement
    public MiscLeaveType getMiscLeaveType() {
        return miscLeaveType;
    }

    @XmlElement
    public LocalDate getBeginDate() {
        return beginDate;
    }

    @XmlElement
    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public String getViewType() {
        return "miscleave-grant";
    }
}
