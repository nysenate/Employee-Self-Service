package gov.nysenate.ess.seta.client.view;

import gov.nysenate.ess.core.client.view.PayPeriodView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.seta.model.accrual.PeriodAccSummary;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlRootElement(name = "Accruals")
public class AccrualsView implements ViewObject
{
    protected PayPeriodView payPeriod;
    protected boolean computed;
    protected AccrualStateView empState;

    protected BigDecimal sickAccruedYtd;
    protected BigDecimal personalAccruedYtd;
    protected BigDecimal vacationAccruedYtd;
    protected BigDecimal serviceYtd;
    protected BigDecimal serviceYtdExpected;

    protected BigDecimal sickBanked;
    protected BigDecimal vacationBanked;

    protected BigDecimal empSickUsed;
    protected BigDecimal famSickUsed;
    protected BigDecimal personalUsed;
    protected BigDecimal vacationUsed;
    protected BigDecimal holidayUsed;
    protected BigDecimal miscUsed;

    protected BigDecimal vacationRate;
    protected BigDecimal sickRate;

    /** --- Constructors --- */

    public AccrualsView(PeriodAccSummary pac) {
        if (pac != null) {
            this.payPeriod = new PayPeriodView(pac.getPayPeriod());
            this.computed = pac.isComputed();
            if (this.computed) {
                this.empState = new AccrualStateView(pac.getEmpAccrualState());
            }
            this.sickAccruedYtd = pac.getEmpHoursAccrued();
            this.personalAccruedYtd = pac.getPerHoursAccrued();
            this.vacationAccruedYtd = pac.getVacHoursAccrued();
            this.serviceYtd = pac.getTotalHoursYtd();
            this.serviceYtdExpected = pac.getExpectedTotalHours();
            this.sickBanked = pac.getEmpHoursBanked();
            this.vacationBanked = pac.getVacHoursBanked();
            this.empSickUsed = pac.getEmpHoursUsed();
            this.famSickUsed = pac.getFamHoursUsed();
            this.personalUsed = pac.getPerHoursUsed();
            this.vacationUsed = pac.getVacHoursUsed();
            this.holidayUsed = pac.getHolHoursUsed();
            this.miscUsed = pac.getMiscHoursUsed();
            this.vacationRate = pac.getVacRate();
            this.sickRate = pac.getSickRate();
        }
    }

    /** --- Functional Getters --- */

    public BigDecimal getVacationAvailable() {
        return this.vacationAccruedYtd.add(this.vacationBanked).subtract(this.vacationUsed);
    }

    public BigDecimal getPersonalAvailable() {
        return this.personalAccruedYtd.subtract(this.personalUsed);
    }

    public BigDecimal getSickAvailable() {
        return this.sickAccruedYtd.add(this.sickBanked).subtract(this.empSickUsed).subtract(this.famSickUsed);
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
    public BigDecimal getSickBanked() {
        return sickBanked;
    }

    @XmlElement
    public BigDecimal getVacationBanked() {
        return vacationBanked;
    }

    @XmlElement
    public BigDecimal getEmpSickUsed() {
        return empSickUsed;
    }

    @XmlElement
    public BigDecimal getFamSickUsed() {
        return famSickUsed;
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
    public BigDecimal getVacationRate() {
        return vacationRate;
    }

    @XmlElement
    public BigDecimal getSickRate() {
        return sickRate;
    }

    @Override
    public String getViewType() {
        return "accruals";
    }
}