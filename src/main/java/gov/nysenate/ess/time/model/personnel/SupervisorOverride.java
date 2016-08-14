package gov.nysenate.ess.time.model.personnel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class SupervisorOverride
{
    /** The supervisor that's getting permission to view another supervisor's employees. */
    protected int granteeSupervisorId;

    /** If true, this override is effective. */
    protected boolean active;

    /** The supervisor that's granting permissions to the grantee supervisor to manage their records. */
    protected int granterSupervisorId;

    /** Optional start date for which this override is effective from. */
    protected Optional<LocalDate> startDate;

    /** Optional end date for which this override is effective until. */
    protected Optional<LocalDate> endDate;

    /** Audit Dates. */
    protected LocalDateTime originDate;
    protected LocalDateTime updateDate;

    /** --- Constructors --- */

    public SupervisorOverride() {}

    /** --- Basic Getters/Setters --- */

    public int getGranteeSupervisorId() {
        return granteeSupervisorId;
    }

    public void setGranteeSupervisorId(int granteeSupervisorId) {
        this.granteeSupervisorId = granteeSupervisorId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getGranterSupervisorId() {
        return granterSupervisorId;
    }

    public void setGranterSupervisorId(int granterSupervisorId) {
        this.granterSupervisorId = granterSupervisorId;
    }

    public Optional<LocalDate> getStartDate() {
        return startDate;
    }

    public void setStartDate(Optional<LocalDate> startDate) {
        this.startDate = startDate;
    }

    public Optional<LocalDate> getEndDate() {
        return endDate;
    }

    public void setEndDate(Optional<LocalDate> endDate) {
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
}
