package gov.nysenate.ess.time.client.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.time.model.attendance.AttendanceHours;

import javax.xml.bind.annotation.XmlElement;
import java.math.BigDecimal;

public class AttendanceHoursView implements ViewObject {

    protected BigDecimal workHours;
    protected BigDecimal travelHours;
    protected BigDecimal holidayHours;
    protected BigDecimal vacationHours;
    protected BigDecimal personalHours;
    protected BigDecimal sickEmpHours;
    protected BigDecimal sickFamHours;
    protected BigDecimal miscHours;
    protected BigDecimal totalHours;

    protected AttendanceHoursView() {}

    public AttendanceHoursView(AttendanceHours hours) {
        if (hours != null) {
            this.workHours = hours.getWorkHours().orElse(null);
            this.travelHours = hours.getTravelHours().orElse(null);
            this.holidayHours = hours.getHolidayHours().orElse(null);
            this.vacationHours = hours.getVacationHours().orElse(null);
            this.personalHours = hours.getPersonalHours().orElse(null);
            this.sickEmpHours = hours.getSickEmpHours().orElse(null);
            this.sickFamHours = hours.getSickFamHours().orElse(null);
            this.miscHours = hours.getMiscHours().orElse(null);
            this.totalHours = hours.getTotalHours();
        }
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
    public BigDecimal getTotalHours() {
        return totalHours;
    }

    @Override
    public String getViewType() {
        return "attendance-hours";
    }
}
