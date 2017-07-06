package gov.nysenate.ess.time.client.view.expectedhrs;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.time.model.expectedhrs.ExpectedHours;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.time.LocalDate;

@XmlRootElement
public class ExpectedHoursView implements ViewObject {

    protected int year;

    protected LocalDate beginDate;
    protected LocalDate endDate;

    protected BigDecimal yearlyHoursExpected;
    protected BigDecimal ytdHoursExpected;
    protected BigDecimal periodEndHoursExpected;
    protected BigDecimal periodHoursExpected;

    public ExpectedHoursView(ExpectedHours expectedHours) {
        if (expectedHours == null) {
            return;
        }
        this.year = expectedHours.getYear();
        this.beginDate = expectedHours.getBeginDate();
        this.endDate = expectedHours.getEndDate();
        this.yearlyHoursExpected = expectedHours.getYearlyHoursExpected();
        this.ytdHoursExpected = expectedHours.getYtdHoursExpected();
        this.periodEndHoursExpected = expectedHours.getPeriodEndHoursExpected();
        this.periodHoursExpected = expectedHours.getPeriodHoursExpected();
    }

    @Override
    public String getViewType() {
        return "expected-hours";
    }

    @XmlElement
    public int getYear() {
        return year;
    }

    @XmlElement
    public LocalDate getBeginDate() {
        return beginDate;
    }

    @XmlElement
    public LocalDate getEndDate() {
        return endDate;
    }

    @XmlElement
    public BigDecimal getYearlyHoursExpected() {
        return yearlyHoursExpected;
    }

    @XmlElement
    public BigDecimal getYtdHoursExpected() {
        return ytdHoursExpected;
    }

    @XmlElement
    public BigDecimal getPeriodEndHoursExpected() {
        return periodEndHoursExpected;
    }

    @XmlElement
    public BigDecimal getPeriodHoursExpected() {
        return periodHoursExpected;
    }
}
