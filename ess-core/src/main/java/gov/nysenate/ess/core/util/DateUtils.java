package gov.nysenate.ess.core.util;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtils
{
    /** --- Reference Dates --- */

    public static final LocalDate LONG_AGO = LocalDate.of(1970, 1, 1);
    public static final LocalDate THE_FUTURE = LocalDate.of(2999, 12, 31);
    public static final Range<LocalDate> ALL_DATES = Range.closed(LONG_AGO, THE_FUTURE);
    public static final Range<LocalDateTime> ALL_DATE_TIMES = Range.closed(LONG_AGO.atStartOfDay(), atEndOfDay(THE_FUTURE));

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
     * Given the LocalDate range, extract the lower bound LocalDate. If the lower bound is not set,
     * a really early date will be returned. If the bound is open, a single day will be added to the
     * LocalDate. If its closed, the date will remain as is.
     *
     * @param localDateRange Range<LocalDate>
     * @return LocalDate - Lower bound in the date range
     */
    public static LocalDate startOfDateRange(Range<LocalDate> localDateRange) {
        if (localDateRange != null) {
            LocalDate lower;
            if (localDateRange.hasLowerBound()) {
                lower = (localDateRange.lowerBoundType().equals(BoundType.CLOSED))
                        ? localDateRange.lowerEndpoint() : localDateRange.lowerEndpoint().plusDays(1);
            }
            else {
                lower = LocalDate.ofYearDay(1, 1);
            }
            return lower;
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
            LocalDate upper;
            if (localDateRange.hasUpperBound()) {
                upper = (localDateRange.upperBoundType().equals(BoundType.CLOSED))
                        ? localDateRange.upperEndpoint() : localDateRange.upperEndpoint().minusDays(1);
            }
            else {
                upper = LocalDate.ofYearDay(2999, 1);
            }
            return upper;
        }
        throw new IllegalArgumentException("Supplied localDateRange is null.");
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
        if (dateTimeRange != null) {
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
        throw new IllegalArgumentException("Supplied localDateTimeRange is null.");
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

}
