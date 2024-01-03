package gov.nysenate.ess.time.service.accrual;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public interface DonationService {
    /**
     * Gets the total hours the given employee has ever donated.
     */
    BigDecimal getHoursDonated(int empId, int year);

    /**
     * Maps each PayPeriod to the hours donated in that period for a given employee.
     */
    Map<LocalDate, BigDecimal> getHoursDonated(int empId, int startYear, LocalDate endDate);
}
