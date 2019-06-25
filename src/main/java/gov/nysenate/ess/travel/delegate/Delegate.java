package gov.nysenate.ess.travel.delegate;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.personnel.Employee;

import java.time.LocalDate;

public class Delegate {
    protected int id;
    protected Employee principal;
    protected Employee delegate;
    protected LocalDate startDate;
    protected LocalDate endDate;

    public Delegate(int id, Employee principal, Employee delegate, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.principal = principal;
        this.delegate = delegate;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Delegate(Employee principal, Employee delegate, LocalDate startDate, LocalDate endDate) {
        this(0, principal, delegate, startDate, endDate);
    }

    public boolean isActive() {
        return Range.closed(startDate, endDate).contains(LocalDate.now());
    }
}
