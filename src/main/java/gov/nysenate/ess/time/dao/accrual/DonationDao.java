package gov.nysenate.ess.time.dao.accrual;

import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.personnel.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DonationDao {
    Multimap<LocalDate, BigDecimal> getDonatedTime(int empId, Range<LocalDate> dateRange);

    /**
     * Returns the total time an employee donated in this calendar year.
     */
    default BigDecimal getTimeDonatedThisYear(int empId) {
        var yearData = getDonatedTime(empId, LocalDate.now().getYear());
        return yearData.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Maps effective dates to a list of hours donated for a given year and employee.
     * Must be a multimap in case multiple donations are on the same day.
     * @return The result map, which may be empty if no donations were made that year.
     */
    default Multimap<LocalDate, BigDecimal> getDonatedTime(int empId, int year) {
        var dateRange = Range.closed(LocalDate.ofYearDay(year, 1),
                LocalDate.ofYearDay(year + 1, 1).minusDays(1));
        return getDonatedTime(empId, dateRange);
    }

    /**
     * @return if submission was successful
     */
    boolean submitDonation(Employee emp, BigDecimal donation);
}
