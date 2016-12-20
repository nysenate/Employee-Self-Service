package gov.nysenate.ess.time.model.accrual;

import gov.nysenate.ess.time.model.accrual.AccrualSummary;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a summary of accruals over a given year.
 */
public class AnnualAccSummary extends AccrualSummary
{
    protected int year;
    protected LocalDate endDate;
    protected LocalDate closeDate;
    protected LocalDate contServiceDate;
    protected int payPeriodsYtd;
    protected int payPeriodsBanked;

    protected LocalDateTime updateDate;

    /** --- Constructors --- */

    public AnnualAccSummary() {}

    /** --- Basic Getters/Setters --- */

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(LocalDate closeDate) {
        this.closeDate = closeDate;
    }

    public LocalDate getContServiceDate() {
        return contServiceDate;
    }

    public void setContServiceDate(LocalDate contServiceDate) {
        this.contServiceDate = contServiceDate;
    }

    public int getPayPeriodsYtd() {
        return payPeriodsYtd;
    }

    public void setPayPeriodsYtd(int payPeriodsYtd) {
        this.payPeriodsYtd = payPeriodsYtd;
    }

    public int getPayPeriodsBanked() {
        return payPeriodsBanked;
    }

    public void setPayPeriodsBanked(int payPeriodsBanked) {
        this.payPeriodsBanked = payPeriodsBanked;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }
}