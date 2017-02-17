package gov.nysenate.ess.time.client.view.payroll;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.time.model.payroll.Deduction;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlRootElement
public class DeductionView implements ViewObject
{
    protected String code;
    protected int order;
    protected String description;
    protected BigDecimal amount;

    public DeductionView(Deduction deduction) {
        this.code = deduction.getCode();
        this.order = deduction.getOrder();
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
    public int getOrder() {
        return order;
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
