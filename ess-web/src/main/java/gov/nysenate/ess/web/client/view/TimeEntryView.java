package gov.nysenate.ess.web.client.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.ess.web.client.view.base.ViewObject;
import gov.nysenate.ess.web.model.attendance.TimeEntry;
import gov.nysenate.ess.web.model.payroll.MiscLeaveType;
import gov.nysenate.ess.core.model.payroll.PayType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

@XmlRootElement
public class TimeEntryView implements ViewObject
{

    protected String entryId;
    protected String timeRecordId;
    protected int empId;
    protected String employeeName;
    protected LocalDate date;
    protected BigDecimal workHours;
    protected BigDecimal travelHours;
    protected BigDecimal holidayHours;
    protected BigDecimal vacationHours;
    protected BigDecimal personalHours;
    protected BigDecimal sickEmpHours;
    protected BigDecimal sickFamHours;
    protected BigDecimal miscHours;
    protected String miscType;
    protected boolean active;
    protected String empComment;
    protected String payType;
    protected String txOriginalUserId;
    protected String txUpdateUserId;
    protected LocalDateTime txOriginalDate;
    protected LocalDateTime txUpdateDate;

    public TimeEntryView() {}

    public TimeEntryView(TimeEntry entry) {
        if (entry != null) {
            this.entryId = entry.getEntryId() != null ? entry.getEntryId().toString() : null;
            this.timeRecordId = entry.getTimeRecordId() != null ? entry.getTimeRecordId().toString() : null;
            this.empId = entry.getEmpId();
            this.employeeName = entry.getEmployeeName();
            this.date = entry.getDate();
            this.workHours = entry.getWorkHours().orElse(null);
            this.travelHours = entry.getTravelHours().orElse(null);
            this.holidayHours = entry.getHolidayHours().orElse(null);
            this.vacationHours = entry.getVacationHours().orElse(null);
            this.personalHours = entry.getPersonalHours().orElse(null);
            this.sickEmpHours = entry.getSickEmpHours().orElse(null);
            this.sickFamHours = entry.getSickFamHours().orElse(null);
            this.miscHours = entry.getMiscHours().orElse(null);
            this.miscType = entry.getMiscType() != null ? entry.getMiscType().name() : null;
            this.active = entry.isActive();
            this.empComment = entry.getEmpComment();
            this.payType = entry.getPayType() != null ? entry.getPayType().name() : null;
            this.txOriginalUserId = entry.getOriginalUserId();
            this.txUpdateUserId = entry.getUpdateUserId();
            this.txOriginalDate = entry.getOriginalDate();
            this.txUpdateDate = entry.getUpdateDate();
        }
    }

    @JsonIgnore
    public TimeEntry toTimeEntry() {
        TimeEntry entry = new TimeEntry(
                timeRecordId != null && timeRecordId.matches("\\d+") ? new BigInteger(timeRecordId) : null,
                empId);
        entry.setEntryId(entryId != null ? new BigInteger(entryId) : null);
        entry.setEmployeeName(employeeName);
        entry.setDate(date);
        entry.setWorkHours(workHours);
        entry.setTravelHours(travelHours);
        entry.setHolidayHours(holidayHours);
        entry.setVacationHours(vacationHours);
        entry.setPersonalHours(personalHours);
        entry.setSickEmpHours(sickEmpHours);
        entry.setSickFamHours(sickFamHours);
        entry.setMiscHours(miscHours);
        entry.setMiscType(miscType != null ? MiscLeaveType.valueOf(miscType) : null);
        entry.setActive(active);
        entry.setEmpComment(empComment);
        entry.setPayType(payType != null ? PayType.valueOf(payType) : null);
        entry.setOriginalUserId(txOriginalUserId);
        entry.setUpdateUserId(txUpdateUserId);
        entry.setOriginalDate(txOriginalDate);
        entry.setUpdateDate(txUpdateDate);
        return entry;
    }

    @XmlElement
    public String getEntryId() {
        return entryId;
    }

    @XmlElement
    public String getTimeRecordId() {
        return timeRecordId;
    }

    @XmlElement
    public int getEmpId() {
        return empId;
    }

    @XmlElement
    public String getEmployeeName() {
        return employeeName;
    }

    @XmlElement
    public LocalDate getDate() {
        return date;
    }

    @XmlElement
    public BigDecimal getWorkHours() {
        return workHours;
    }

    @XmlElement
    public BigDecimal getTravelHours() {
        return travelHours;
    }

    @XmlElement
    public BigDecimal getHolidayHours() {
        return holidayHours;
    }

    @XmlElement
    public BigDecimal getVacationHours() {
        return vacationHours;
    }

    @XmlElement
    public BigDecimal getPersonalHours() {
        return personalHours;
    }

    @XmlElement
    public BigDecimal getSickEmpHours() {
        return sickEmpHours;
    }

    @XmlElement
    public BigDecimal getSickFamHours() {
        return sickFamHours;
    }

    @XmlElement
    public BigDecimal getMiscHours() {
        return miscHours;
    }

    @XmlElement
    public String getMiscType() {
        return miscType;
    }

    @XmlElement
    public boolean isActive() {
        return active;
    }

    @XmlElement
    public String getEmpComment() {
        return empComment;
    }

    @XmlElement
    public String getPayType() {
        return payType;
    }

    @XmlElement
    public String getTxOriginalUserId() {
        return txOriginalUserId;
    }

    @XmlElement
    public String getTxUpdateUserId() {
        return txUpdateUserId;
    }

    @XmlElement
    public LocalDateTime getTxOriginalDate() {
        return txOriginalDate;
    }

    @XmlElement
    public LocalDateTime getTxUpdateDate() {
        return txUpdateDate;
    }

    @Override
    @XmlElement
    public String getViewType() {
        return "time entry";
    }
}
