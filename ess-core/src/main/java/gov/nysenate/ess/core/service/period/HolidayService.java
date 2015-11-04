package gov.nysenate.ess.core.service.period;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.period.Holiday;
import gov.nysenate.ess.core.util.SortOrder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service layer interface to retrieve holidays.
 */
public interface HolidayService
{
    /**
     * Returns a holiday if it exists for the given date. Throws exception otherwise.
     *
     * @param date Date
     * @return Holiday
     */
    public Optional<Holiday> getHoliday(LocalDate date);

    /**
     * Retrieves a list of all the holidays that occur between the given dates inclusively in order
     * of earliest first.
     *
     * @param dateRange Range<LocalDate> - The date range to search.
     * @param includeQuestionable boolean - Include questionable holidays
     * @param dateOrder SortOrder - Order the results by date.
     * @return List<Holiday>
     */
    public List<Holiday> getHolidays(Range<LocalDate> dateRange, boolean includeQuestionable, SortOrder dateOrder);
}
