package gov.nysenate.ess.time.model.attendance;

import gov.nysenate.ess.time.model.payroll.MiscLeaveType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

public class TimeOffRequestDay extends AttendanceHours implements Comparable<TimeOffRequestDay> {

    public static final Comparator<TimeOffRequestDay> defaultComparator =
            Comparator.comparing(TimeOffRequestDay::getDate);

    protected int requestId;
    protected LocalDate date;
    protected MiscLeaveType miscType;
    protected MiscLeaveType miscType2;

    public TimeOffRequestDay() {}

    public TimeOffRequestDay(int requestId, LocalDate date, BigDecimal workHours, BigDecimal holidayHours,
                             BigDecimal vacationHours, BigDecimal personalHours, BigDecimal sickEmpHours,
                             BigDecimal sickFamHours, BigDecimal miscHours, MiscLeaveType miscType,
                             BigDecimal misc2Hours, MiscLeaveType miscType2) {
        this.requestId = requestId;
        this.date = date;
        this.workHours = workHours;
        this.holidayHours = holidayHours;
        this.vacationHours = vacationHours;
        this.personalHours = personalHours;
        this.sickEmpHours = sickEmpHours;
        this.sickFamHours = sickFamHours;
        this.miscHours = miscHours;
        this.miscType = miscType;
        this.misc2Hours = misc2Hours;
        this.miscType2 = miscType2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeOffRequestDay)) return false;
        if (!super.equals(o)) return false;
        TimeOffRequestDay day = (TimeOffRequestDay) o;
        return getRequestId() == day.getRequestId() &&
                getDate().equals(day.getDate()) &&
                getMiscType() == day.getMiscType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getRequestId(), getDate(), getMiscType(), getMiscType2());
    }

    /*Basic Getters and Setters*/
    public int getRequestId() {
        return requestId;
    }

    public LocalDate getDate() {
        return date;
    }

    public MiscLeaveType getMiscType() {
        return miscType;
    }

    public MiscLeaveType getMiscType2() {
        return miscType2;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public void setDate(LocalDate date) { this.date = date; }

    public void setMiscType(MiscLeaveType miscType) {
        this.miscType = miscType;
    }

    public void setMiscType2(MiscLeaveType miscType2) {
        this.miscType2 = miscType2;
    }

    @Override
    public int compareTo(TimeOffRequestDay t2) {
        return defaultComparator.compare(this, t2);
    }
}
