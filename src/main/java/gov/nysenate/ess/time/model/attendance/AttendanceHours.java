package gov.nysenate.ess.time.model.attendance;

import com.google.common.base.Objects;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Contains numerical fields for each NYSenate attendance type
 * along with some utility functions
 */
public abstract class AttendanceHours {

    protected BigDecimal workHours;
    protected BigDecimal travelHours;
    protected BigDecimal holidayHours;
    protected BigDecimal vacationHours;
    protected BigDecimal personalHours;
    protected BigDecimal sickEmpHours;
    protected BigDecimal sickFamHours;
    protected BigDecimal miscHours;

    public AttendanceHours() {}

    public AttendanceHours(AttendanceHours other) {
        this.workHours = other.workHours;
        this.travelHours = other.travelHours;
        this.holidayHours = other.holidayHours;
        this.vacationHours = other.vacationHours;
        this.personalHours = other.personalHours;
        this.sickEmpHours = other.sickEmpHours;
        this.sickFamHours = other.sickFamHours;
        this.miscHours = other.miscHours;
    }

    /** --- Functional Getters / Setters --- */

    public BigDecimal getTotalHours() {
        return BigDecimal.ZERO
                .add(this.getWorkHours().orElse(BigDecimal.ZERO))
                .add(this.getTravelHours().orElse(BigDecimal.ZERO))
                .add(this.getHolidayHours().orElse(BigDecimal.ZERO))
                .add(this.getMiscHours().orElse(BigDecimal.ZERO))
                .add(this.getPersonalHours().orElse(BigDecimal.ZERO))
                .add(this.getSickEmpHours().orElse(BigDecimal.ZERO))
                .add(this.getSickFamHours().orElse(BigDecimal.ZERO))
                .add(this.getVacationHours().orElse(BigDecimal.ZERO));
    }

    public Optional<BigDecimal> getWorkHours() {
        return Optional.ofNullable(workHours);
    }

    public Optional<BigDecimal> getTravelHours() {
        return Optional.ofNullable(travelHours);
    }

    public Optional<BigDecimal> getHolidayHours() {
        return Optional.ofNullable(holidayHours);
    }

    public Optional<BigDecimal> getVacationHours() {
        return Optional.ofNullable(vacationHours);
    }

    public Optional<BigDecimal> getPersonalHours() {
        return Optional.ofNullable(personalHours);
    }

    public Optional<BigDecimal> getSickEmpHours() {
        return Optional.ofNullable(sickEmpHours);
    }

    public Optional<BigDecimal> getSickFamHours() {
        return Optional.ofNullable(sickFamHours);
    }

    public Optional<BigDecimal> getMiscHours() {
        return Optional.ofNullable(miscHours);
    }

    public boolean isEmpty() {
        return workHours == null &&
                travelHours == null &&
                holidayHours == null &&
                vacationHours == null &&
                personalHours == null &&
                sickEmpHours == null &&
                sickFamHours == null &&
                miscHours == null;
    }

    /** --- Overridden Methods --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AttendanceHours)) return false;
        AttendanceHours that = (AttendanceHours) o;
        return Objects.equal(workHours, that.workHours) &&
                Objects.equal(travelHours, that.travelHours) &&
                Objects.equal(holidayHours, that.holidayHours) &&
                Objects.equal(vacationHours, that.vacationHours) &&
                Objects.equal(personalHours, that.personalHours) &&
                Objects.equal(sickEmpHours, that.sickEmpHours) &&
                Objects.equal(sickFamHours, that.sickFamHours) &&
                Objects.equal(miscHours, that.miscHours);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(workHours, travelHours, holidayHours, vacationHours, personalHours, sickEmpHours, sickFamHours, miscHours);
    }

    /** --- Setters --- */

    public void setWorkHours(BigDecimal workHours) {
        this.workHours = workHours;
    }

    public void setTravelHours(BigDecimal travelHours) {
        this.travelHours = travelHours;
    }

    public void setHolidayHours(BigDecimal holidayHours) {
        this.holidayHours = holidayHours;
    }

    public void setVacationHours(BigDecimal vacationHours) {
        this.vacationHours = vacationHours;
    }

    public void setPersonalHours(BigDecimal personalHours) {
        this.personalHours = personalHours;
    }

    public void setSickEmpHours(BigDecimal sickEmpHours) {
        this.sickEmpHours = sickEmpHours;
    }

    public void setSickFamHours(BigDecimal sickFamHours) {
        this.sickFamHours = sickFamHours;
    }

    public void setMiscHours(BigDecimal miscHours) {
        this.miscHours = miscHours;
    }
}
