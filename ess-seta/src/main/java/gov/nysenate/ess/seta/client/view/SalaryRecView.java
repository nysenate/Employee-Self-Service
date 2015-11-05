package gov.nysenate.ess.seta.client.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.payroll.SalaryRec;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.time.LocalDate;

@XmlRootElement
public class SalaryRecView implements ViewObject
{

    protected BigDecimal salaryRate;
    protected String payType;

    private LocalDate effectDate;
    private LocalDate endDate;

    public SalaryRecView(SalaryRec rec) {
        if (rec != null) {
            this.salaryRate = rec.getSalaryRate();
            this.payType = rec.getPayType().name();
            this.effectDate = rec.getEffectDate();
            this.endDate = rec.getEndDate();
        }
    }

    @Override
    public String getViewType() {
        return "salary rec";
    }

    @XmlElement
    public BigDecimal getSalaryRate() {
        return salaryRate;
    }

    @XmlElement
    public String getPayType() {
        return payType;
    }

    @XmlElement
    public LocalDate getEffectDate() {
        return effectDate;
    }

    @XmlElement
    public LocalDate getEndDate() {
        return endDate;
    }
}
