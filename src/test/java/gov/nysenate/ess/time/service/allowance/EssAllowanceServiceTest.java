package gov.nysenate.ess.time.service.allowance;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.SillyTest;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.time.model.allowances.AllowanceUsage;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

@Category(SillyTest.class)
public class EssAllowanceServiceTest extends BaseTest
{
    private static final Logger logger = LoggerFactory.getLogger(EssAllowanceServiceTest.class);

    @Autowired
    EssAllowanceService allowanceService;

    @Test
    public void getAllowanceTest() {
        AllowanceUsage usage = allowanceService.getAllowanceUsage(11303, 2015);
        logger.info("{}", OutputUtils.toJson(usage));
    }

    @Test
    public void getAllowanceAtDateTest() {
        int empId = 12250;
        LocalDate aptDate = LocalDate.of(2017, 4, 17);
        LocalDate secondPeriodDate = LocalDate.of(2017, 4, 20);

        AllowanceUsage initialUsage = allowanceService.getAllowanceUsage(empId, aptDate);
        assertEquals("Initial hours used are 0", BigDecimal.ZERO, initialUsage.getHoursUsed());
        assertEquals("Initial money used is 0", BigDecimal.ZERO, initialUsage.getMoneyUsed());

        AllowanceUsage secondPerUsage = allowanceService.getAllowanceUsage(empId, secondPeriodDate);
        assertEquals("Second per. hour usage is 4", new BigDecimal(4), secondPerUsage.getHoursUsed());
        assertEquals("Second per. money used is 48", new BigDecimal(48), secondPerUsage.getMoneyUsed());

    }
}
