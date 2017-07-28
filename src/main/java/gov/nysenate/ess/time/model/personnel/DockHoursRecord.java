package gov.nysenate.ess.time.model.personnel;

import com.google.common.collect.Range;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Brian Heitner
 *
 * This is a data object structure for holding Docked Hours information.
 */
public class DockHoursRecord {
    protected Integer employeeId;
    protected LocalDate beginDate;
    protected LocalDate endDate;
    protected BigDecimal dockHours = BigDecimal.ZERO;
    protected LocalDateTime createdDate;
    protected LocalDateTime updatedDate;

    /** --- Functional Getters / Setters --- */

    public Range<LocalDate> getDateRange() {
        return Range.closedOpen(beginDate, endDate.plusDays(1));
    }

    /** --- Getters / Setters --- */

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(LocalDate beginDate) {
        this.beginDate = beginDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public void setDockHours(BigDecimal dockHours) {
        this.dockHours = dockHours;
    }

    public BigDecimal getDockHours() {
        return this.dockHours;
    }

}
