package gov.nysenate.ess.time.model.accrual;

import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.transaction.TransactionRecord;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * This class is intended to be used within the accrual dao layer. It's purpose is to hold relevant data
 * when computing accruals for a time period in which summary records in the database don't exist.
 */
public class AccrualGap
{
    protected int empId;
    protected LocalDate startDate;
    protected LocalDate endDate;

    protected List<PayPeriod> gapPeriods;
    protected LinkedList<TransactionRecord> recordsDuringGap;
    protected LinkedList<PeriodAccUsage> periodUsageRecs;

    /** --- Constructors --- */

    public AccrualGap(int empId, LocalDate startDate, LocalDate endDate) {
        this.empId = empId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /** --- Functional Getters/Setters --- */

    /**
     * Filter transaction records such that only those that occur in the given pay period will be
     * returned. The order is maintained.
     */
    @Deprecated
    public LinkedList<TransactionRecord> getTransRecsDuringPeriod(PayPeriod payPeriod) {
        LinkedList<TransactionRecord> recs = new LinkedList<>();
        for (TransactionRecord rec : recordsDuringGap) {
            /** FIXME 09/12
            if (rec.getEffectDate().compareTo(payPeriod.getStartDate()) >= 0 &&
                    rec.getEffectDate().compareTo(payPeriod.getEndDate()) <= 0) {
                recs.addUsage(rec);
            }
            else {
                break;
            }               */
        }
        return recs;
    }

    /**
     * Return a usage record that is set for the given pay period or return an empty Optional if it doesn't exist.
     */
    public Optional<PeriodAccUsage> getUsageRecDuringPeriod(PayPeriod payPeriod) {
        return periodUsageRecs.stream().filter(r -> r.getPayPeriod().equals(payPeriod)).findFirst();
    }

    /** --- Basic Getters/Setters --- */

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<PayPeriod> getGapPeriods() {
        return gapPeriods;
    }

    public void setGapPeriods(List<PayPeriod> gapPeriods) {
        this.gapPeriods = gapPeriods;
    }

    public LinkedList<TransactionRecord> getRecordsDuringGap() {
        return recordsDuringGap;
    }

    public void setRecordsDuringGap(LinkedList<TransactionRecord> recordsDuringGap) {
        this.recordsDuringGap = recordsDuringGap;
    }

    public LinkedList<PeriodAccUsage> getPeriodUsageRecs() {
        return periodUsageRecs;
    }

    public void setPeriodUsageRecs(LinkedList<PeriodAccUsage> periodUsageRecs) {
        this.periodUsageRecs = periodUsageRecs;
    }
}