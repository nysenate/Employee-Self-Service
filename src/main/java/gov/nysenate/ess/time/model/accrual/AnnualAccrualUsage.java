package gov.nysenate.ess.time.model.accrual;

import gov.nysenate.ess.time.model.accrual.AccrualUsage;

import java.time.LocalDate;

/**
 * Helper class to store accrual usage sums for a given year. Note that this class does
 * not contain information about the hours accrued, just how much was used.
 */
public class AnnualAccrualUsage extends AccrualUsage
{
    protected LocalDate latestStartDate;
    protected LocalDate latestEndDate;

    public AnnualAccrualUsage() {}

    /** --- Functional Getters/Setters --- */

    public int getYear() {
        if (latestStartDate != null) {
            return latestStartDate.getYear();
        }
        throw new IllegalStateException("The latest start date was not set for accrual usage. Cannot retrieve year!");
    }

    /** --- Basic Getters/Setters --- */

    public LocalDate getLatestStartDate() {
        return latestStartDate;
    }

    public void setLatestStartDate(LocalDate latestStartDate) {
        this.latestStartDate = latestStartDate;
    }

    public LocalDate getLatestEndDate() {
        return latestEndDate;
    }

    public void setLatestEndDate(LocalDate latestEndDate) {
        this.latestEndDate = latestEndDate;
    }
}