package gov.nysenate.ess.seta.model.allowances;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
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
    protected BigDecimal yearlyAllowance;

    /** The amount of money that has been paid out this year (according to  transactions) */
    protected BigDecimal baseMoneyUsed;

    /** The amount of money used as recorded in submitted time records for periods not covered in transaction history */
    protected BigDecimal recordMoneyUsed;

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

    public void addSalaryRecs(Collection<SalaryRec> salaryRecs) {
        salaryRecs.forEach(rec -> salaryRecMap.put(rec.getEffectiveRange(), rec));
    }

    public SalaryRec getSalaryRec(LocalDate date) {
        return salaryRecMap.get(date);
    }

    public ImmutableList<SalaryRec> getSalaryRecs() {
        return ImmutableList.copyOf(salaryRecMap.asMapOfRanges().values());
    }

    public BigDecimal getRecordCost(TimeRecord record) {
        return record.getTimeEntries().stream()
                .filter(entry -> entry.getPayType() == PayType.TE)
                .map(entry -> entry.getWorkHours().orElse(BigDecimal.ZERO)
                        .multiply(getSalaryRec(entry.getDate()).getSalaryRate()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
}
