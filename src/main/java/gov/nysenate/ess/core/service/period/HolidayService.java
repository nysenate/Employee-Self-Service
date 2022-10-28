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
     * Returns a holiday if it exists for the given date, is active, and is non-questionable
     *
     * @param date LocalDate
     * @return {@link Optional<Holiday>}
     */
    Optional<Holiday> getActiveHoliday(LocalDate date);

    /**
     * Retrieves a list of all the holidays that occur between the given dates inclusively in
     * ascending order by date.
     *
     * @param includeQuestionable boolean - Include questionable holidays
     * @return List<Holiday>
     */
    List<Holiday> getHolidays(LocalDate fromDate, LocalDate toDate, boolean includeQuestionable);
}
