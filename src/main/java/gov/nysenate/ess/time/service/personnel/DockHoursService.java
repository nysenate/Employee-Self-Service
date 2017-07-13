package gov.nysenate.ess.time.service.personnel;

import com.google.common.collect.Range;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author Brian Heitner
 *
 * Defines a service that can provide information about an employee's expected hours.
 */
public interface DockHoursService {

    /**
     * Get the Docked Hours within a given Date Range
     *
     * @param empId int - employee id
     * @param dateRange Range<LocalDate> - Date range used to find Total Docked Hours
     * @return BigDecimal - Total Docked Hours within the date range
     */

    BigDecimal getDockHours(int empId,Range<LocalDate> dateRange);

}
