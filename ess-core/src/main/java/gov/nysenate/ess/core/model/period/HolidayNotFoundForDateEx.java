package gov.nysenate.ess.core.model.period;

import java.time.LocalDate;

public class HolidayNotFoundForDateEx extends HolidayException
{
    private static final long serialVersionUID = 7100960898017208118L;

    protected LocalDate requestedDate;

    public HolidayNotFoundForDateEx(LocalDate date) {
        super("Holiday not found for date " + date.toString());
        requestedDate = date;
    }

    public LocalDate getRequestedDate() {
        return requestedDate;
    }
}
