package gov.nysenate.ess.time.client.view.allowances;

import gov.nysenate.ess.core.client.view.PayPeriodView;
import gov.nysenate.ess.time.model.allowances.PeriodAllowanceUsage;

import javax.xml.bind.annotation.XmlElement;
import java.math.BigDecimal;

public class PeriodAllowanceUsageView extends AllowanceUsageView {

    private PayPeriodView payPeriod;

    private BigDecimal periodHoursUsed;
    private BigDecimal periodMoneyUsed;

    public PeriodAllowanceUsageView(PeriodAllowanceUsage allowanceUsage) {
        super(allowanceUsage);
        this.payPeriod = new PayPeriodView(allowanceUsage.getPayPeriod());
        this.periodHoursUsed = allowanceUsage.getPeriodHoursUsed();
        this.periodMoneyUsed = allowanceUsage.getPeriodMoneyUsed();
    }

    @XmlElement
    public PayPeriodView getPayPeriod() {
        return payPeriod;
    }

    @XmlElement
    public BigDecimal getPeriodHoursUsed() {
        return periodHoursUsed;
    }

    @XmlElement
    public BigDecimal getPeriodMoneyUsed() {
        return periodMoneyUsed;
    }

    @Override
    public String getViewType() {
        return "period-" + super.getViewType();
    }
}
