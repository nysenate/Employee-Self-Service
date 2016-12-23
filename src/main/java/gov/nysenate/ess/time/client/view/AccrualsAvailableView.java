package gov.nysenate.ess.time.client.view;

import gov.nysenate.ess.core.client.view.PayPeriodView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.time.model.accrual.AccrualsAvailable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlRootElement(name = "AccrualsAvailable")
public class AccrualsAvailableView implements ViewObject
{
    private int empId;
    private PayPeriodView payPeriod;

    private BigDecimal personalAvailable;
    private BigDecimal vacationAvailable;
    private BigDecimal sickAvailable;

    private BigDecimal serviceYtdExpected;
    private BigDecimal serviceYtd;
    private BigDecimal biWeekHrsExpected;

    public AccrualsAvailableView(AccrualsAvailable accrualsAvailable) {
        this.empId = accrualsAvailable.getEmpId();
        this.payPeriod = new PayPeriodView(accrualsAvailable.getPayPeriod());
        this.personalAvailable = accrualsAvailable.getPersonalAvailable();
        this.vacationAvailable = accrualsAvailable.getVacationAvailable();
        this.sickAvailable = accrualsAvailable.getSickAvailable();
        this.serviceYtdExpected = accrualsAvailable.getServiceYtdExpected();
        this.serviceYtd = accrualsAvailable.getServiceYtd();
        this.biWeekHrsExpected = accrualsAvailable.getBiWeekHrsExpected();
    }

    @Override
    public String getViewType() {
        return "accruals-available";
    }

    @XmlElement
    public int getEmpId() {
        return empId;
    }

    @XmlElement
    public PayPeriodView getPayPeriod() {
        return payPeriod;
    }

    @XmlElement
    public BigDecimal getPersonalAvailable() {
        return personalAvailable;
    }

    @XmlElement
    public BigDecimal getVacationAvailable() {
        return vacationAvailable;
    }

    @XmlElement
    public BigDecimal getSickAvailable() {
        return sickAvailable;
    }

    @XmlElement
    public BigDecimal getServiceYtdExpected() {
        return serviceYtdExpected;
    }

    @XmlElement
    public BigDecimal getServiceYtd() {
        return serviceYtd;
    }

    @XmlElement
    public BigDecimal getBiWeekHrsExpected() {
        return biWeekHrsExpected;
    }
}
