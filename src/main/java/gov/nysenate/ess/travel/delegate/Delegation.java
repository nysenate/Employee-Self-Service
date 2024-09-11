package gov.nysenate.ess.travel.delegate;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.authorization.role.TravelRole;

import java.time.LocalDate;

public class Delegation {

    protected int id;
    protected TravelRole role;
    protected Employee principal;
    protected Employee delegate;
    protected LocalDate startDate;
    protected LocalDate endDate;

    public Delegation(int id, TravelRole role, Employee principal, Employee delegate, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.role = role;
        this.principal = principal;
        this.delegate = delegate;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Delegation(TravelRole role, Employee principal, Employee delegate, LocalDate startDate, LocalDate endDate) {
        this(0, role, principal, delegate, startDate, endDate);
    }

    public TravelRole role() {
        return role;
    }

    /**
     * Get the principal employee for this delegation.
     * @return
     */
    public Employee principal() {
        return this.principal;
    }

    /**
     * Get the delegate
     * @return
     */
    public Employee delegate() {
        return this.delegate;
    }

    /**
     * @return true if the current date is between this delegation's startDate and endDate.
     */
    public boolean isActive() {
        return Range.closed(startDate, endDate).contains(LocalDate.now());
    }

    /**
     * @return true if this delegation is scheduled to take effect in the future.
     */
    public boolean isScheduled() {
        return LocalDate.now().isBefore(startDate);
    }

    /**
     * @return true if the {@code endDate} of this delegation has passed.
     */
    public boolean isExpired() {
        return endDate.isBefore(LocalDate.now());
    }
}
