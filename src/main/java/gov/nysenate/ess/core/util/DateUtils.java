package gov.nysenate.ess.core.util;

import com.google.common.collect.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.EnumSet;


public class DateUtils
{
    /** --- Reference Dates --- */

    public static final LocalDate LONG_AGO = LocalDate.of(1970, 1, 1);
    public static final LocalDate THE_FUTURE = LocalDate.of(2999, 12, 31);
    public static final Range<LocalDate> ALL_DATES = Range.closed(LONG_AGO, THE_FUTURE);
    public static final Range<LocalDateTime> ALL_DATE_TIMES = Range.closed(LONG_AGO.atStartOfDay(), atEndOfDay(THE_FUTURE));

    public static final DateTimeFormatter SFMS_DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

    /**
     * The number of months BEFORE the standard year that the Senate Fiscal Year starts
     * (with the Senate fiscal year being identified by the year in which the fiscal year ends)
     */
    public static final Period SENATE_FISCAL_YEAR_OFFSET = Period.ofMonths(9);

    public static final ImmutableSet<DayOfWeek> WEEKEND = ImmutableSet.copyOf(
            EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
    );
    /**
     * Returns a LocalDateTime that represents the time just before the start of the next day.
     */
    public static LocalDateTime atEndOfDay(LocalDate date) {
        return date.atTime(23, 59, 59, 999999999);
    }


    /**
     * Returns a LocalDate that is set to a date way in the past. Can't really use the LocalDate.MIN
     * value because it doesn't play nice when converting into a database date.
     *
     * @return LocalDate
     */
    public static LocalDate longAgo() {
        return LocalDate.ofYearDay(1, 1);
    }

    /**
     * Return a range of dates corresponding to all of the days in the given year
     *  range is in the form [Jan 1 {year}, Jan 1 {year + 1})
     * @param year int
     * @return Range<LocalDate> - all of the days in the given year
     */
    public static Range<LocalDate> yearDateRange(int year) {
        return Range.closedOpen(LocalDate.ofYearDay(year, 1), LocalDate.ofYearDay(year + 1, 1));
    }

    /**
     * Return a range of dates corresponding to
     * all of the days in the Senate fiscal year that ENDS DURING the given year
     *
     * @param endYear int - the ending year of the requested fiscal year
     * @return Range<LocalDate> - all dates during the requested fiscal year
     */
    public static Range<LocalDate> fiscalYearDateRange(int endYear) {
        LocalDate startDate = LocalDate.ofYearDay(endYear, 1)
                .minus(SENATE_FISCAL_YEAR_OFFSET);
        LocalDate nextYearStartDate = LocalDate.ofYearDay(endYear + 1, 1)
                .minus(SENATE_FISCAL_YEAR_OFFSET);
        return Range.closedOpen(startDate, nextYearStartDate);
    }

    /**
     * Gets the senate fiscal year of the given date
     * @param localDate LocalDate
     * @return int - fiscal year of given date
     */
    public static int getFiscalYear(LocalDate localDate) {
        return localDate.plus(SENATE_FISCAL_YEAR_OFFSET).getYear();
    }

    /**
     * Converts a date range to a range of years enclosing the date range
     * @param dateRange Range<LocalDate>
     * @param fiscalYears boolean - will return range of fiscal years if true
     * @return Range<Integer> - a closed open range containing all years covered by the date range
     */
    public static Range<Integer> toYearRange(Range<LocalDate> dateRange, boolean fiscalYears) {
        Integer lowerEndpointYear = null;
        Integer upperEndpointYear = null;
        Range<LocalDate> canonicalRange = dateRange.canonical(getLocalDateDiscreteDomain());
        if (canonicalRange.hasLowerBound()) {
            LocalDate lowerEndpoint = canonicalRange.lowerEndpoint();
            lowerEndpointYear = fiscalYears ? getFiscalYear(lowerEndpoint) : lowerEndpoint.getYear();
        }
        if (canonicalRange.hasUpperBound()) {
            LocalDate upperEndpoint = canonicalRange.upperEndpoint().minusDays(1);
            upperEndpointYear = fiscalYears ? getFiscalYear(upperEndpoint) : upperEndpoint.getYear();
            upperEndpointYear += 1;  // the upper endpoint is closed
        }
        if (upperEndpointYear == null && lowerEndpointYear == null) {
            return Range.all();
        }
        if (lowerEndpointYear == null) {
            return Range.lessThan(upperEndpointYear);
        }
        if (upperEndpointYear == null) {
            return Range.atMost(lowerEndpointYear);
        }
        return Range.closedOpen(lowerEndpointYear, upperEndpointYear);
    }

    /**
     * Given the LocalDate range, extract the lower bound LocalDate. If the lower bound is not set,
     * a really early date will be returned. If the bound is open, a single day will be added to the
     * LocalDate. If its closed, the date will remain as is.
     *
     * @param localDateRange Range<LocalDate>
     * @return LocalDate - Lower bound in the date range
     */
    public static LocalDate startOfDateRange(Range<LocalDate> localDateRange) {
        if (localDateRange != null) {
            Range<LocalDate> canonicalRange = localDateRange.canonical(getLocalDateDiscreteDomain());
            if (!canonicalRange.hasLowerBound()) {
                return LONG_AGO;
            }
            return canonicalRange.lowerEndpoint();
        }
        throw new IllegalArgumentException("Supplied localDateRange is null.");
    }

    /**
     * Given the LocalDate range, extract the upper bound LocalDate. If the upper bound is not set, a
     * date far in the future will be returned. If the bound is open, a single day will be subtracted
     * from the LocalDate. If its closed, the date will remain as is.
     *
     * @param localDateRange Range<LocalDate>
     * @return LocalDate - Upper bound in the date range
     */
    public static LocalDate endOfDateRange(Range<LocalDate> localDateRange) {
        if (localDateRange != null) {
            Range<LocalDate> canonicalRange = localDateRange.canonical(getLocalDateDiscreteDomain());
            if (!canonicalRange.hasUpperBound()) {
                return THE_FUTURE;
            }
            return canonicalRange.upperEndpoint().minusDays(1);
        }
        throw new IllegalArgumentException("Supplied localDateRange is null.");
    }

    /**
     * Converts the given date range to a date time range
     * The resulting date time range is in the format [start of earliest day, start of latest day + 1)
     * @param localDateRange Range<LocalDate>
     * @return Range<LocalDateTime>
     */
    public static Range<LocalDateTime> toDateTimeRange(Range<LocalDate> localDateRange) {
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;
        if (localDateRange.hasLowerBound()) {
            startDateTime = startOfDateRange(localDateRange).atStartOfDay();
        }
        if (localDateRange.hasUpperBound()) {
            endDateTime = endOfDateRange(localDateRange).plusDays(1).atStartOfDay();
        }
        if (startDateTime == null && endDateTime == null) {
            return Range.all();
        }
        if (startDateTime == null) {
            return Range.lessThan(endDateTime);
        }
        if (endDateTime == null) {
            return Range.atLeast(startDateTime);
        }
        return Range.closedOpen(startDateTime, endDateTime);
    }

    /**
     * Return true if the date is a weekday
     * @param date LocalDate
     * @return Boolean
     */
    public static boolean isWeekday(LocalDate date) {
        return !WEEKEND.contains(date.getDayOfWeek());
    }

    /**
     * Get the number of weekdays during the given date range
     * @param dateRange Range<LocalDate>
     * @return Integer
     */
    public static long getNumberOfWeekdays(Range<LocalDate> dateRange) {
        if (!(dateRange.hasLowerBound() && dateRange.hasUpperBound())) {
            throw new IllegalStateException("Cannot get number of work days in an unbounded date range: " + dateRange);
        }
        long workDays = 0;
        LocalDate startDate = startOfDateRange(dateRange);
        LocalDate endDate = endOfDateRange(dateRange);

        // Cut the date range into a week divisible form.  Add all cut days to a running total (if they are work days)
        while (DayOfWeek.MONDAY != startDate.getDayOfWeek() && startDate.isBefore(endDate)) {
            if (isWeekday(startDate)) {
                workDays++;
            }
            startDate = startDate.plusDays(1);
        }
        while (DayOfWeek.SUNDAY != endDate.getDayOfWeek() && !startDate.isAfter(endDate)) {
            if (isWeekday(endDate)) {
                workDays++;
            }
            endDate = endDate.minusDays(1);
        }

        // Once the range is divisible by 7 (if it hasn't been reduced to nothing),
        // get the total days in the range and multiply by 5/7.  Add to running total
        if (startDate.isBefore(endDate)) {
            long totalRangeDays = ChronoUnit.DAYS.between(startDate, endDate.plusDays(1));
            workDays += totalRangeDays * 5 / 7;
        }

        return workDays;
    }

    /**
     * Get the number of weekdays during the given date range set
     * @see #getNumberOfWeekdays(Range)
     * @param dateRange RangeSet<LocalDate>
     * @return Integer
     */
    public static long getNumberOfWeekdays(RangeSet<LocalDate> dateRange) {
        return dateRange.asRanges().stream()
                .map(DateUtils::getNumberOfWeekdays)
                .reduce(0L, Long::sum);
    }

    /**
     * Convert a LocalDateTime to a Date.
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Convert a LocalDate to a Date.
     */
    public static Date toDate(LocalDate localDate) {
        if (localDate == null) return null;
        return toDate(localDate.atStartOfDay());
    }

    /**
     * Convert a Date to a LocalDateTime at the system's default time zone.
     */
    public static LocalDateTime getLocalDateTime(Date date) {
        if (date == null) return null;
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * Convert a Date to a LocalDate at the system's default time zone.
     */
    public static LocalDate getLocalDate(Date date) {
        if (date == null) return null;
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate();
        }
        return getLocalDateTime(date).toLocalDate();
    }

    /**
     * Given the LocalDateTime range, extract the lower bound LocalDateTime. If the lower bound is not set,
     * a really early date will be returned. If the bound is open, a single nanosecond will be added to the
     * LocalDateTime. If its closed, the dateTime will remain as is.
     *
     * @param dateTimeRange Range<LocalDateTime>
     * @return LocalDateTime - Lower bound in the dateTime range
     */
    public static LocalDateTime startOfDateTimeRange(Range<LocalDateTime> dateTimeRange) {
        if (dateTimeRange != null) {
            LocalDateTime lower;
            if (dateTimeRange.hasLowerBound()) {
                lower = (dateTimeRange.lowerBoundType().equals(BoundType.CLOSED))
                        ? dateTimeRange.lowerEndpoint() : dateTimeRange.lowerEndpoint().plusNanos(1);
            }
            else {
                lower = LONG_AGO.atStartOfDay();
            }
            return lower;
        }
        throw new IllegalArgumentException("Supplied localDateTimeRange is null.");
    }

    /**
     * Given the LocalDateTime range, extract the upper bound LocalDateTime. If the upper bound is not set, a
     * date far in the future will be returned. If the bound is open, a single nanosecond will be subtracted
     * from the LocalDateTime. If its closed, the date will remain as is.
     *
     * @param dateTimeRange Range<LocalDateTime>
     * @return LocalDateTime - Upper bound in the dateTime range
     */
    public static LocalDateTime endOfDateTimeRange(Range<LocalDateTime> dateTimeRange) {
        if (dateTimeRange == null) {
            throw new IllegalArgumentException("Supplied localDateTimeRange is null.");
        }
        LocalDateTime upper;
        if (dateTimeRange.hasUpperBound()) {
            upper = (dateTimeRange.upperBoundType().equals(BoundType.CLOSED))
                    ? dateTimeRange.upperEndpoint() : dateTimeRange.upperEndpoint().minusNanos(1);
        }
        else {
            upper = atEndOfDay(THE_FUTURE);
        }
        return upper;
    }

    /**
     * Returns a new LocalDate for the first day of the year prior to the one in the given 'date'.
     * For example, given date 'Sep 2, 2015', this will return 'Jan 1, 2014'.
     *
     * @param date LocalDate
     * @return LocalDate
     */
    public static LocalDate firstDayOfPreviousYear(LocalDate date) {
        return LocalDate.from(date).minusYears(1).withDayOfYear(1);
    }



    /**
     * --- Discrete Domains ---
     * Custom implementations of the DiscreteDomain class for various data types that enable more range functionality
     */
    public static DiscreteDomain<LocalDate> getLocalDateDiscreteDomain() {
        return LocalDateDomain.INSTANCE;
    }
    private static final class LocalDateDomain extends DiscreteDomain<LocalDate>
    {
        private static final LocalDateDomain INSTANCE = new LocalDateDomain();

        @Override
        public LocalDate next(LocalDate value) {
            return (LocalDate.MAX.equals(value)) ? null : value.plusDays(1);
        }

        @Override
        public LocalDate previous(LocalDate value) {
            return (LocalDate.MIN.equals(value)) ? null : value.minusDays(1);
        }

        @Override
        public long distance(LocalDate start, LocalDate end) {
            return Period.between(start, end).getDays();
        }

        @Override
        public LocalDate minValue() {
            return LONG_AGO;
        }

        @Override
        public LocalDate maxValue() {
            return THE_FUTURE;
        }
    }

}
