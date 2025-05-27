package gov.nysenate.ess.time.model.accrual;

import gov.nysenate.ess.core.model.period.PayPeriod;

import java.math.BigDecimal;

/**
 * Contains available accruals for a given pay period in addition to year to date hours balance
 */
public class AccrualsAvailable {

    private int empId;
    private PayPeriod payPeriod;

    private BigDecimal personalAvailable;
    private BigDecimal vacationAvailable;
    private BigDecimal sickAvailable;

    private BigDecimal serviceYtdExpected;
    private BigDecimal serviceYtd;
    private BigDecimal biWeekHrsExpected;

    public AccrualsAvailable(AccrualSummary summary, PayPeriod period,
                             BigDecimal serviceYtdExpected, BigDecimal biWeekHrsExpected,
                             BigDecimal sickHoursDonated) {
        this.empId = summary.getEmpId();
        this.payPeriod = period;

        this.personalAvailable = summary.getPerHoursAccrued()
                .subtract(summary.getPerHoursUsed());

        this.vacationAvailable = summary.getVacHoursBanked()
                .add(summary.getVacHoursAccrued())
                .subtract(summary.getVacHoursUsed());

        this.sickAvailable = summary.getEmpHoursBanked()
                .add(summary.getEmpHoursAccrued())
                .subtract(summary.getEmpHoursUsed())
                .subtract(summary.getFamHoursUsed())
                .subtract(sickHoursDonated);

        this.serviceYtdExpected = serviceYtdExpected;
        this.serviceYtd = summary.getTotalHoursUsed();
        this.biWeekHrsExpected = biWeekHrsExpected;
    }

    public int getEmpId() {
        return empId;
    }

    public PayPeriod getPayPeriod() {
        return payPeriod;
    }

    public BigDecimal getPersonalAvailable() {
        return personalAvailable;
    }

    public BigDecimal getVacationAvailable() {
        return vacationAvailable;
    }

    public BigDecimal getSickAvailable() {
        return sickAvailable;
    }

    public BigDecimal getServiceYtdExpected() {
        return serviceYtdExpected;
    }

    public BigDecimal getServiceYtd() {
        return serviceYtd;
    }

    public BigDecimal getBiWeekHrsExpected() {
        return biWeekHrsExpected;
    }
}
