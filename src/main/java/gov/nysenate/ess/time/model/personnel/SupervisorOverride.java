package gov.nysenate.ess.time.model.personnel;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.DateUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class SupervisorOverride
{
    /** The supervisor that's getting permission to view and approve additional employees. */
    private int granteeEmpId;

    /** The supervisor that's granting permissions to the grantee supervisor to manage their records.
     *  OR the single employee that is being granted if this is an employee override*/
    private int granterEmpId;

    /** Specifies whether this override is granting all employees under a supervisor or a single employee */
    private SupOverrideType supOverrideType;

    /** If true, this override is effective. */
    private boolean active;

    /** Optional start date for which this override is effective from. */
    private LocalDate startDate;

    /** Optional end date for which this override is effective up to. */
    private LocalDate endDate;

    /** Audit Dates. */
    private LocalDateTime originDate;
    private LocalDateTime updateDate;

    /* --- Constructors --- */

    public SupervisorOverride() {}

    /* --- Functional Getters / Setters --- */

    public Optional<LocalDate> getStartDate() {
        return Optional.ofNullable(startDate);
    }

    public Optional<LocalDate> getEndDate() {
        return Optional.ofNullable(endDate);
    }

    public Range<LocalDate> getEffectiveDateRange() {
        Range<LocalDate> effectiveRange;
        if (startDate == null && endDate == null) {
            effectiveRange =  Range.all();
        }
        else if (endDate == null) {
            effectiveRange = Range.atLeast(startDate);
        }
        else if (startDate == null) {
            effectiveRange =  Range.atMost(endDate);
        } else {
            effectiveRange = Range.closed(startDate, endDate);
        }

        return effectiveRange.canonical(DateUtils.getLocalDateDiscreteDomain());
    }

    public boolean isInEffect() {
        return isActive() && getEffectiveDateRange().contains(LocalDate.now());
    }

    /* --- Overrides --- */

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("granteeEmpId", granteeEmpId)
                .append("granterEmpId", granterEmpId)
                .append("supOverrideType", supOverrideType)
                .append("active", active)
                .append("startDate", startDate)
                .append("endDate", endDate)
                .append("originDate", originDate)
                .append("updateDate", updateDate)
                .toString();
    }

    /* --- Basic Getters/Setters --- */

    public int getGranteeEmpId() {
        return granteeEmpId;
    }

    public void setGranteeEmpId(int granteeEmpId) {
        this.granteeEmpId = granteeEmpId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getGranterEmpId() {
        return granterEmpId;
    }

    public void setGranterEmpId(int granterEmpId) {
        this.granterEmpId = granterEmpId;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getOriginDate() {
        return originDate;
    }

    public void setOriginDate(LocalDateTime originDate) {
        this.originDate = originDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public SupOverrideType getSupOverrideType() {
        return supOverrideType;
    }

    public void setSupOverrideType(SupOverrideType supOverrideType) {
        this.supOverrideType = supOverrideType;
    }
}
