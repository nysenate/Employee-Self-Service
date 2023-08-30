package gov.nysenate.ess.time.dao.accrual;

import com.google.common.collect.Multimap;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DonationDao {
    BigDecimal getTimeDonatedInLastYear(int empId);

    /**
     * Maps effective dates to a list of hours donated for a given year and employee.
     * Must be a multimap in case multiple donations are on the same day.
     * @return The result map, which may be empty if no donations were made that year.
     */
    Multimap<LocalDate, BigDecimal> getDonatedTime(int empId, int year);

    /**
     * @return if submission was successful
     */
    boolean submitDonation(int empId, BigDecimal donation);
}
