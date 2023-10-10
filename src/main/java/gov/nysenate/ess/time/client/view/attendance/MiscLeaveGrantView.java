package gov.nysenate.ess.time.client.view.attendance;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.time.model.payroll.MiscLeaveGrant;
import gov.nysenate.ess.time.model.payroll.MiscLeaveType;

import javax.xml.bind.annotation.XmlElement;
import java.math.BigDecimal;
import java.time.LocalDate;

public class MiscLeaveGrantView implements ViewObject {
    private final int empId;
    private final MiscLeaveType miscLeaveType;
    private final LocalDate beginDate;
    private final LocalDate endDate;
    private final BigDecimal hours;

    public MiscLeaveGrantView(MiscLeaveGrant grant) {
        this.empId = grant.empId();
        this.miscLeaveType = grant.miscLeaveType();
        this.beginDate = grant.beginDate();
        this.endDate = grant.endDate();
        this.hours = grant.hours();
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

    @XmlElement
    public BigDecimal getHours() {
        return hours;
    }

    @Override
    public String getViewType() {
        return "miscleave-grant";
    }
}
