package gov.nysenate.ess.time.model.expectedhrs;

import com.google.common.collect.Range;

import java.time.LocalDate;

public class InvalidExpectedHourDatesEx extends RuntimeException {

    protected LocalDate beginDate;
    protected LocalDate endDate;

    public InvalidExpectedHourDatesEx(LocalDate beginDate, LocalDate endDate) {
        super("Invalid expected hour dates.  beginDate: " + beginDate + "  endDate: " + endDate);
        this.beginDate = beginDate;
        this.endDate = endDate;
    }

    public Range<LocalDate> getDateRange() {
        return Range.closed(beginDate, endDate);
    }

    public LocalDate getBeginDate() {
        return beginDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
