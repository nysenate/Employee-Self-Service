package gov.nysenate.ess.time.client.view.attendance;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.time.model.attendance.TimeOffRequestDay;
import gov.nysenate.ess.time.model.payroll.MiscLeaveType;

import java.time.LocalDate;
import java.time.ZoneId;

public class TimeOffRequestDayView implements ViewObject {

    protected int requestId;
    protected LocalDate date;
    protected int workHours;
    protected int holidayHours;
    protected int vacationHours;
    protected int personalHours;
    protected int sickEmpHours;
    protected int sickFamHours;
    protected int miscHours;
    protected String miscType;

    public TimeOffRequestDayView() {}

    public TimeOffRequestDayView(TimeOffRequestDay day) {
        this.requestId = day.getRequestId();
        this.date = LocalDate.parse(day.getDate().toString());
        this.workHours = day.getWorkHours();
        this.holidayHours = day.getHolidayHours();
        this.vacationHours = day.getVacationHours();
        this.personalHours = day.getPersonalHours();
        this.sickEmpHours = day.getSickEmpHours();
        this.sickFamHours = day.getSickFamHours();
        this.miscHours = day.getMiscHours();
        this.miscType = day.getMiscType().toString();
    }


    public TimeOffRequestDay toTimeOffRequestDay() {
        TimeOffRequestDay day = new TimeOffRequestDay();
        day.setRequestId(requestId);
        day.setWorkHours(workHours);
        day.setHolidayHours(holidayHours);
        day.setVacationHours(vacationHours);
        day.setPersonalHours(personalHours);
        day.setSickEmpHours(sickEmpHours);
        day.setSickFamHours(sickFamHours);
        day.setMiscHours(miscHours);
        day.setMiscType(miscType != null ? MiscLeaveType.valueOf(miscType) : null);
        day.setDate(java.sql.Date.from(date.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant()));
        return day;
    }

    public int getRequestId() {
        return requestId;
    }

    public LocalDate getDate() {
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

    public String getMiscType() {
        return miscType;
    }

    @Override
    public String getViewType() {
        return "time-off-request-day";
    }
}
