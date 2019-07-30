package gov.nysenate.ess.time.model.attendance;

import gov.nysenate.ess.time.model.payroll.MiscLeaveType;

import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

public class TimeOffRequestDay implements Comparable<TimeOffRequestDay> {

    public static final Comparator<TimeOffRequestDay> defaultComparator =
            Comparator.comparing(TimeOffRequestDay::getDate);

    protected int requestId;
    protected Date date;
    protected int workHours;
    protected int holidayHours;
    protected int vacationHours;
    protected int personalHours;
    protected int sickEmpHours;
    protected int sickFamHours;
    protected int miscHours;
    protected MiscLeaveType miscType;

    public TimeOffRequestDay() {}

    public TimeOffRequestDay(int requestId, Date date, int workHours, int holidayHours,
                             int vacationHours, int personalHours, int sickEmpHours,
                             int sickFamHours, int miscHours, MiscLeaveType miscType) {
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
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeOffRequestDay that = (TimeOffRequestDay) o;
        return requestId == that.requestId &&
                workHours == that.workHours &&
                holidayHours == that.holidayHours &&
                vacationHours == that.vacationHours &&
                personalHours == that.personalHours &&
                sickEmpHours == that.sickEmpHours &&
                sickFamHours == that.sickFamHours &&
                miscHours == that.miscHours &&
                date.equals(that.date) &&
                miscType == that.miscType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, date, workHours, holidayHours, vacationHours,
                personalHours, sickEmpHours, sickFamHours, miscHours, miscType);
    }

    /*Basic Getters and Setters*/
    public int getRequestId() {
        return requestId;
    }

    public Date getDate() {
        return date;
    }

    public int getWorkHours() {
        return workHours;
    }

    public int getHolidayHours() {
        return holidayHours;
    }

    public int getVacationHours() {
        return vacationHours;
    }

    public int getPersonalHours() {
        return personalHours;
    }

    public int getSickEmpHours() {
        return sickEmpHours;
    }

    public int getSickFamHours() {
        return sickFamHours;
    }

    public int getMiscHours() {
        return miscHours;
    }

    public MiscLeaveType getMiscType() {
        return miscType;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setWorkHours(int workHours) {
        this.workHours = workHours;
    }

    public void setHolidayHours(int holidayHours) {
        this.holidayHours = holidayHours;
    }

    public void setVacationHours(int vacationHours) {
        this.vacationHours = vacationHours;
    }

    public void setPersonalHours(int personalHours) {
        this.personalHours = personalHours;
    }

    public void setSickEmpHours(int sickEmpHours) {
        this.sickEmpHours = sickEmpHours;
    }

    public void setSickFamHours(int sickFamHours) {
        this.sickFamHours = sickFamHours;
    }

    public void setMiscHours(int miscHours) {
        this.miscHours = miscHours;
    }

    public void setMiscType(MiscLeaveType miscType) {
        this.miscType = miscType;
    }

    @Override
    public int compareTo(TimeOffRequestDay t2) {
        return defaultComparator.compare(this, t2);
    }
}
