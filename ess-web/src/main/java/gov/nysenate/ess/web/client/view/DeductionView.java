package gov.nysenate.ess.web.client.view;

import gov.nysenate.ess.web.client.view.base.ViewObject;
import gov.nysenate.ess.web.model.payroll.Deduction;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlRootElement
public class DeductionView implements ViewObject
{
    protected String code;
    protected String description;
    protected BigDecimal amount;

    public DeductionView(Deduction deduction) {
        this.code = deduction.getCode();
        this.description = deduction.getDescription();
        this.amount = deduction.getAmount();
    }

    @Override
    @XmlElement
    public String getViewType() {
        return "Deduction";
    }

    @XmlElement
    public String getCode() {
        return code;
    }

    @XmlElement
    public String getDescription() {
        return description;
    }

    @XmlElement
    public BigDecimal getAmount() {
        return amount;
    }
}
