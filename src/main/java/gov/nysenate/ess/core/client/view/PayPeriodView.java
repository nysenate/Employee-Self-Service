package gov.nysenate.ess.core.client.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;

@XmlRootElement(name = "payPeriod")
public class PayPeriodView implements ViewObject
{
    protected String payPeriodNum;
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected String type;
    protected int numDays;
    protected int numWeekDays;
    protected boolean startYearSplit;
    protected boolean endYearSplit;
    protected boolean active;
    protected boolean current;

    public PayPeriodView() {}

    public PayPeriodView(PayPeriod payPeriod) {
        if (payPeriod != null) {
            this.startDate = payPeriod.getStartDate();
            this.endDate = payPeriod.getEndDate();
            this.payPeriodNum = payPeriod.getPayPeriodNum();
            this.type = payPeriod.getType().toString();
            this.numDays = payPeriod.getNumDaysInPeriod();
            this.numWeekDays = payPeriod.getNumWeekDaysInPeriod();
            this.startYearSplit = payPeriod.isStartOfYearSplit();
            this.endYearSplit = payPeriod.isEndOfYearSplit();
            this.active = payPeriod.isActive();
            this.current = payPeriod.getDateRange().contains(LocalDate.now());
        }
    }

    @JsonIgnore
    public PayPeriod toPayPeriod() {
        return new PayPeriod(
                PayPeriodType.valueOf(type),
                startDate, endDate,
                payPeriodNum, active
        );
    }

    /** --- Basic Getters --- */

    @Override
    @XmlElement
    public String getViewType() {
        return "pay period";
    }

    @XmlElement
    public String getPayPeriodNum() {
        return payPeriodNum;
    }

    @XmlElement
    public LocalDate getStartDate() {
        return startDate;
    }

    @XmlElement
    public LocalDate getEndDate() {
        return endDate;
    }

    @XmlElement
    public String getType() {
        return type;
    }

    @XmlElement
    public int getNumDays() {
        return numDays;
    }

    @XmlElement
    public int getNumWeekDays() {
        return numWeekDays;
    }

    @XmlElement
    public boolean isStartYearSplit() {
        return startYearSplit;
    }

    @XmlElement
    public boolean isEndYearSplit() {
        return endYearSplit;
    }

    @XmlElement
    public boolean isCurrent() {
        return current;
    }

    @XmlElement
    public boolean isActive() {
        return active;
    }
}