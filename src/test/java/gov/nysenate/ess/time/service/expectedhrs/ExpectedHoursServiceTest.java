package gov.nysenate.ess.time.service.expectedhrs;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.SillyTest;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.service.period.PayPeriodService;
import gov.nysenate.ess.time.model.expectedhrs.ExpectedHours;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;

import static gov.nysenate.ess.core.model.period.PayPeriodType.AF;

@Category(SillyTest.class)
public class ExpectedHoursServiceTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(ExpectedHoursServiceTest.class);

    @Autowired ExpectedHoursService expectedHoursService;
    @Autowired PayPeriodService periodService;

    @Test
    public void expHrsTest() {
        int empId = 12393;
        LocalDate date = LocalDate.of(2017, 11, 16);
        PayPeriod period = periodService.getPayPeriod(AF, date);
        Range<LocalDate> dateRange = period.getDateRange();

        BigDecimal expectedHourValue = expectedHoursService.getExpectedHours(12393, period);
        ExpectedHours expectedHours = expectedHoursService.getExpectedHours(empId, dateRange);


        logger.info("\nBd method: {}\nex method: {}", expectedHourValue, expectedHours.getYtdHoursExpected());
    }
}