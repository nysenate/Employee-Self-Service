package gov.nysenate.ess.time.model.expectedhrs;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class ExpectedHours {

    protected LocalDate beginDate;
    protected LocalDate endDate;

    protected BigDecimal yearlyHoursExpected;
    protected BigDecimal ytdHoursExpected;
    protected BigDecimal periodHoursExpected;

    public ExpectedHours(LocalDate beginDate, LocalDate endDate,
                         BigDecimal yearlyHoursExpected, BigDecimal ytdHoursExpected,
                         BigDecimal periodHoursExpected) {
        validateExpectedHourDates(beginDate, endDate);
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.yearlyHoursExpected = Objects.requireNonNull(yearlyHoursExpected);
        this.ytdHoursExpected = Objects.requireNonNull(ytdHoursExpected);
        this.periodHoursExpected = Objects.requireNonNull(periodHoursExpected);
    }

    /* --- Methods --- */

    public static void validateExpectedHourDates(LocalDate beginDate, LocalDate endDate) {
        if (beginDate == null || endDate == null ||
                beginDate.isAfter(endDate) ||
                beginDate.getYear() != endDate.getYear()) {
            throw new InvalidExpectedHourDatesEx(beginDate, endDate);
        }
    }

    /* --- Functional Getters --- */

    public int getYear() {
        return beginDate.getYear();
    }

    public BigDecimal getPeriodEndHoursExpected() {
        return ytdHoursExpected.add(periodHoursExpected);
    }

    /* --- Getters --- */

    public LocalDate getBeginDate() {
        return beginDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public BigDecimal getYearlyHoursExpected() {
        return yearlyHoursExpected;
    }

    public BigDecimal getYtdHoursExpected() {
        return ytdHoursExpected;
    }

    public BigDecimal getPeriodHoursExpected() {
        return periodHoursExpected;
    }
}
