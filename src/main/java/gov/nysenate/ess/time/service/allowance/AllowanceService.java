package gov.nysenate.ess.time.service.allowance;

import gov.nysenate.ess.time.model.allowances.AllowanceUsage;

import java.time.LocalDate;

public interface AllowanceService
{
    /**
     * Gets the allowance usage for a single employee over a given year
     * @param empId int
     * @param year int
     * @return AllowanceUsage
     */
    public AllowanceUsage getAllowanceUsage(int empId, int year);

    /**
     * Get annual allowance usage before the given date
     * @param empId int
     * @param date LocalDate
     * @return {@link AllowanceUsage}
     */
    public AllowanceUsage getAllowanceUsage(int empId, LocalDate date);
}
