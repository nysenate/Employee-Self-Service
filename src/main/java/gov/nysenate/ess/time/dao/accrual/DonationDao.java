package gov.nysenate.ess.time.dao.accrual;

import com.google.common.collect.Multimap;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DonationDao {
    BigDecimal getTimeDonatedInLastYear(int empId, LocalDate date);

    /**
     * Maps effective dates to a list of hours donated.
     * @param empId whose donations we are getting.
     * @return The result map, which may be empty if no donations were made.
     */
    Multimap<LocalDate, Float> getAllDonatedTime(int empId);
}
