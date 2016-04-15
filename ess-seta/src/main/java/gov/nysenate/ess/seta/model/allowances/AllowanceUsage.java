package gov.nysenate.ess.seta.model.allowances;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import gov.nysenate.ess.seta.model.attendance.TimeEntry;
import gov.nysenate.ess.seta.model.attendance.TimeRecord;
import gov.nysenate.ess.core.model.payroll.SalaryRec;
import gov.nysenate.ess.core.model.payroll.PayType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;

public class AllowanceUsage {

    protected int empId;
    protected int year;

    /** The amount of money allowed for the year */
    protected BigDecimal yearlyAllowance = BigDecimal.ZERO;

    /** The amount of money that has been paid out this year (according to  transactions) */
    protected BigDecimal baseMoneyUsed = BigDecimal.ZERO;

    /** The amount of money used as recorded in submitted time records for periods not covered in transaction history */
    protected BigDecimal recordMoneyUsed = BigDecimal.ZERO;

    /** The number of hours that have been paid out this year */
    protected BigDecimal baseHoursUsed = BigDecimal.ZERO;

    /** The number of hours that have been recorded in submitted time entries for periods not covered in transaction history */
    protected BigDecimal recordHoursUsed = BigDecimal.ZERO;

    /** The employees salary recs over the year */
    protected RangeMap<LocalDate, SalaryRec> salaryRecMap = TreeRangeMap.create();

    public AllowanceUsage(int empId, int year) {
        this.empId = empId;
        this.year = year;
    }

    /** --- Functional Getters / Setters --- */

    public BigDecimal getMoneyUsed() {
        return baseMoneyUsed.add(recordMoneyUsed);
    }

    public BigDecimal getHoursUsed() {
        return baseHoursUsed.add(recordHoursUsed);
    }

    public void addSalaryRecs(Collection<SalaryRec> salaryRecs) {
        salaryRecs.forEach(rec -> salaryRecMap.put(rec.getEffectiveRange(), rec));
    }

    public SalaryRec getSalaryRec(LocalDate date) {
        return salaryRecMap.get(date);
    }

    public ImmutableList<SalaryRec> getSalaryRecs() {
        return ImmutableList.copyOf(salaryRecMap.asMapOfRanges().values());
    }

    /**
     * Calculate the cost of a time record using the salary recs
     * @param record TimeRecord
     * @return BigDecimal - record cost
     */
    public BigDecimal getRecordCost(TimeRecord record) {
        return record.getTimeEntries().stream()
                .map(this::getEntryCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get the cost of a single time entry given the salary recs in this AllowanceUsage
     * @param entry TimeEntry
     * @return BigDecimal - entry cost
     */
    public BigDecimal getEntryCost(TimeEntry entry) {
        SalaryRec salaryForDay = salaryRecMap.get(entry.getDate());
        if (PayType.TE != entry.getPayType() || salaryForDay == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal workHours = entry.getWorkHours().orElse(BigDecimal.ZERO);
        return workHours.multiply(salaryForDay.getSalaryRate());
    }

    /** --- Getters / Setters --- */

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public BigDecimal getYearlyAllowance() {
        return yearlyAllowance;
    }

    public void setYearlyAllowance(BigDecimal yearlyAllowance) {
        this.yearlyAllowance = yearlyAllowance;
    }

    public BigDecimal getBaseMoneyUsed() {
        return baseMoneyUsed;
    }

    public void setBaseMoneyUsed(BigDecimal baseMoneyUsed) {
        this.baseMoneyUsed = baseMoneyUsed;
    }

    public BigDecimal getRecordMoneyUsed() {
        return recordMoneyUsed;
    }

    public void setRecordMoneyUsed(BigDecimal recordMoneyUsed) {
        this.recordMoneyUsed = recordMoneyUsed;
    }

    public BigDecimal getBaseHoursUsed() {
        return baseHoursUsed;
    }

    public void setBaseHoursUsed(BigDecimal baseHoursUsed) {
        this.baseHoursUsed = baseHoursUsed;
    }

    public BigDecimal getRecordHoursUsed() {
        return recordHoursUsed;
    }

    public void setRecordHoursUsed(BigDecimal recordHoursUsed) {
        this.recordHoursUsed = recordHoursUsed;
    }
}
