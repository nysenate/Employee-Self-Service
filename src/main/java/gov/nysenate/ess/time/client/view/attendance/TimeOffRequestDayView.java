package gov.nysenate.ess.time.client.view.attendance;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.time.model.attendance.TimeOffRequestDay;
import gov.nysenate.ess.time.model.payroll.MiscLeaveType;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TimeOffRequestDayView extends AttendanceHoursView implements ViewObject {

    protected int requestId;
    protected LocalDate date;
    protected String miscType;

    public TimeOffRequestDayView() {}

    public TimeOffRequestDayView(TimeOffRequestDay day) {
        this.requestId = day.getRequestId();
        this.date = LocalDate.parse(day.getDate().toString());
        this.workHours = day.getWorkHours().orElse(BigDecimal.ZERO);
        this.holidayHours = day.getHolidayHours().orElse(BigDecimal.ZERO);
        this.vacationHours = day.getVacationHours().orElse(BigDecimal.ZERO);
        this.personalHours = day.getPersonalHours().orElse(BigDecimal.ZERO);
        this.sickEmpHours = day.getSickEmpHours().orElse(BigDecimal.ZERO);
        this.sickFamHours = day.getSickFamHours().orElse(BigDecimal.ZERO);
        this.miscHours = day.getMiscHours().orElse(BigDecimal.ZERO);
        this.miscType = day.getMiscType() != null ? day.getMiscType().toString() : null;
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
        day.setDate(date);
        return day;
    }

    public int getRequestId() {
        return requestId;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getMiscType() {
        return miscType;
    }

    @Override
    public String getViewType() {
        return "time-off-request-day";
    }
}
