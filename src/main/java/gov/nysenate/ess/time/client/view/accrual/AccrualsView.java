package gov.nysenate.ess.time.client.view.accrual;

import gov.nysenate.ess.core.client.view.PayPeriodView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.time.model.accrual.PeriodAccSummary;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.Optional;

@XmlRootElement(name = "Accruals")
public class AccrualsView implements ViewObject
{
    protected PayPeriodView payPeriod;
    protected boolean computed;
    protected boolean submitted;
    protected AccrualStateView empState;

    protected int empId;

    protected String payType = "";

    protected BigDecimal sickAccruedYtd = BigDecimal.ZERO;
    protected BigDecimal personalAccruedYtd = BigDecimal.ZERO;
    protected BigDecimal vacationAccruedYtd = BigDecimal.ZERO;
    protected BigDecimal serviceYtd = BigDecimal.ZERO;
    protected BigDecimal serviceYtdExpected = BigDecimal.ZERO;
    protected BigDecimal biWeekHrsExpected = BigDecimal.ZERO;

    protected BigDecimal sickBanked = BigDecimal.ZERO;
    protected BigDecimal vacationBanked = BigDecimal.ZERO;

    protected BigDecimal sickEmpUsed = BigDecimal.ZERO;
    protected BigDecimal sickFamUsed = BigDecimal.ZERO;
    protected BigDecimal personalUsed = BigDecimal.ZERO;
    protected BigDecimal vacationUsed = BigDecimal.ZERO;
    protected BigDecimal holidayUsed = BigDecimal.ZERO;
    protected BigDecimal miscUsed = BigDecimal.ZERO;
    protected BigDecimal prevTotalHoursYtd = BigDecimal.ZERO;
    protected BigDecimal totalHoursYtd = BigDecimal.ZERO;

    protected BigDecimal biweekSickEmpUsed = BigDecimal.ZERO;
    protected BigDecimal biweekSickFamUsed = BigDecimal.ZERO;
    protected BigDecimal biweekPersonalUsed = BigDecimal.ZERO;
    protected BigDecimal biweekHolidayUsed = BigDecimal.ZERO;
    protected BigDecimal biweekVacationUsed = BigDecimal.ZERO;
    protected BigDecimal biweekMiscUsed = BigDecimal.ZERO;
    protected BigDecimal biweekTravelUsed = BigDecimal.ZERO;

    protected BigDecimal vacationRate = BigDecimal.ZERO;
    protected BigDecimal sickRate = BigDecimal.ZERO;

    protected BigDecimal zeroHours = BigDecimal.ZERO;


    /** --- Constructors --- */

    public AccrualsView(PeriodAccSummary pac) {
        if (pac != null) {
            this.payPeriod = new PayPeriodView(pac.getPayPeriod());
            this.computed = pac.isComputed();
            if (this.computed) {
                this.empState = new AccrualStateView(pac.getEmpAccrualState());
            }
            this.empId = pac.getEmpId();
            this.submitted = pac.isSubmitted();
            this.sickAccruedYtd = pac.getEmpHoursAccrued();
            this.personalAccruedYtd = pac.getPerHoursAccrued();
            this.vacationAccruedYtd = pac.getVacHoursAccrued();
            this.serviceYtd = pac.getTotalHoursYtd();
            this.serviceYtdExpected = pac.getExpectedTotalHours();
            this.biWeekHrsExpected = pac.getExpectedBiweekHours();
            this.sickBanked = pac.getEmpHoursBanked();
            this.vacationBanked = pac.getVacHoursBanked();
            this.sickEmpUsed = pac.getEmpHoursUsed();
            this.sickFamUsed = pac.getFamHoursUsed();
            this.personalUsed = pac.getPerHoursUsed();
            this.vacationUsed = pac.getVacHoursUsed();
            this.holidayUsed = pac.getHolHoursUsed();
            this.miscUsed = pac.getMiscHoursUsed();
            this.vacationRate = pac.getVacRate();
            this.sickRate = pac.getSickRate();
            this.prevTotalHoursYtd =  pac.getPrevTotalHoursYtd();
            this.totalHoursYtd =  pac.getTotalHoursYtd();
            this.biweekSickFamUsed = pac.getBiweekFamHoursUsed();
            this.biweekSickEmpUsed = pac.getBiweekEmpHoursUsed();
            this.biweekMiscUsed = pac.getBiweekMiscHoursUsed();
            this.biweekVacationUsed = pac.getBiweekVacHoursUsed();
            this.biweekPersonalUsed = pac.getBiweekPerHoursUsed();
            this.biweekHolidayUsed = pac.getBiweekHolHoursUsed();
            this.biweekTravelUsed = pac.getBiweekTravelkHours();

        }
    }

    /** --- Functional Getters --- */

    public BigDecimal getVacationAvailable() {
        return Optional.ofNullable(vacationAccruedYtd).orElse(BigDecimal.ZERO)
                .add(Optional.ofNullable(this.vacationBanked).orElse(BigDecimal.ZERO))
                .subtract(Optional.ofNullable(this.vacationUsed).orElse(BigDecimal.ZERO));
    }

    public BigDecimal getPersonalAvailable() {
        return Optional.ofNullable(personalAccruedYtd).orElse(BigDecimal.ZERO)
                .subtract(Optional.ofNullable(personalUsed).orElse(BigDecimal.ZERO));
    }

    public BigDecimal getSickAvailable() {
        return Optional.ofNullable(sickAccruedYtd).orElse(BigDecimal.ZERO)
                .add(Optional.ofNullable(sickBanked).orElse(BigDecimal.ZERO))
                .subtract(Optional.ofNullable(sickEmpUsed).orElse(BigDecimal.ZERO))
                .subtract(Optional.ofNullable(sickFamUsed).orElse(BigDecimal.ZERO));
    }

    /** --- Basic Getters --- */

    @XmlElement
    public PayPeriodView getPayPeriod() {
        return payPeriod;
    }

    @XmlElement
    public boolean isComputed() {
        return computed;
    }

    @XmlElement
    public AccrualStateView getEmpState() {
        return empState;
    }

    @XmlElement
    public BigDecimal getSickAccruedYtd() {
        return sickAccruedYtd;
    }

    @XmlElement
    public BigDecimal getPersonalAccruedYtd() {
        return personalAccruedYtd;
    }

    @XmlElement
    public BigDecimal getVacationAccruedYtd() {
        return vacationAccruedYtd;
    }

    @XmlElement
    public BigDecimal getServiceYtd() {
        return serviceYtd;
    }

    @XmlElement
    public BigDecimal getServiceYtdExpected() {
        return serviceYtdExpected;
    }

    @XmlElement
    public BigDecimal getBiWeekHrsExpected() {
        return biWeekHrsExpected;
    }

    @XmlElement
    public BigDecimal getSickBanked() {
        return sickBanked;
    }

    @XmlElement
    public BigDecimal getVacationBanked() {
        return vacationBanked;
    }

    @XmlElement
    public BigDecimal getsickEmpUsed() {
        return sickEmpUsed;
    }

    @XmlElement
    public BigDecimal getsickFamUsed() {
        return sickFamUsed;
    }

    @XmlElement
    public BigDecimal getPersonalUsed() {
        return personalUsed;
    }

    @XmlElement
    public BigDecimal getVacationUsed() {
        return vacationUsed;
    }

    @XmlElement
    public BigDecimal getHolidayUsed() {
        return holidayUsed;
    }

    @XmlElement
    public BigDecimal getMiscUsed() {
        return miscUsed;
    }

    @XmlElement
    public BigDecimal getBiweekSickEmpUsed() {
        return biweekSickEmpUsed;
    }

    @XmlElement
    public BigDecimal getBiweekSickFamUsed() {
        return biweekSickFamUsed;
    }

    @XmlElement
    public BigDecimal getBiweekHolidayUsed() {
        return biweekHolidayUsed;
    }

    @XmlElement
    public BigDecimal getBiweekPersonalUsed() {
        return biweekPersonalUsed;
    }

    @XmlElement
    public BigDecimal getBiweekVacationUsed() {
        return biweekVacationUsed;
    }

    @XmlElement
    public BigDecimal getBiweekMiscUsed() {
        return biweekMiscUsed;
    }

    @XmlElement
    public BigDecimal getBiweekTravelUsed() {
        return biweekTravelUsed;
    }


    @XmlElement
    public BigDecimal getVacationRate() {
        return vacationRate;
    }

    @XmlElement
    public BigDecimal getSickRate() {
        return sickRate;
    }


    @XmlElement
    public BigDecimal getPrevTotalHoursYtd() {
        return prevTotalHoursYtd;
    }


    @XmlElement
    public BigDecimal getTotalHoursYtd() {
        return totalHoursYtd;
    }

    @XmlElement
    public BigDecimal getZeroHours() {
        return zeroHours;
    }

    @XmlElement
    public boolean getSubmitted() {
        return submitted;
    }

    @XmlElement
    public int getEmpId() {
        return empId;
    }

    @Override
    public String getViewType() {
        return "accruals";
    }
}