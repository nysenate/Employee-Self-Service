package gov.nysenate.ess.time.service.personnel;

import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.SillyTest;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.transaction.EmpTransactionService;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.RangeUtils;
import gov.nysenate.ess.time.model.expectedhrs.ExpectedHours;
import gov.nysenate.ess.time.service.expectedhrs.ExpectedHoursService;
import gov.nysenate.ess.time.util.AccrualUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;

@Category(SillyTest.class)
public class DockHoursTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(DockHoursTest.class);

    @Autowired private DockHoursService dockHoursService;
    @Autowired private ExpectedHoursService expHoursService;
    @Autowired private EmployeeInfoService empInfoService;
    @Autowired private EmpTransactionService transactionService;

    @Test
    public void Test() {
        Set<Integer> activeEmployeeIds = empInfoService.getActiveEmpIds();
        Range<LocalDate> yearRange = DateUtils.yearDateRange(2016);
        logger.info("finding doc hours emps");
        for (int empId : activeEmployeeIds) {
            BigDecimal dockHours = dockHoursService.getDockHours(empId, yearRange);
            if (dockHours.compareTo(BigDecimal.ZERO) > 0) {
                logger.info("{}\t {}hrs", empId, dockHours);
                TransactionHistory transHistory = transactionService.getTransHistory(empId);
                TreeMap<LocalDate, BigDecimal> effectiveMinHours = transHistory.getEffectiveMinHours(yearRange);

                RangeSet<LocalDate> expectedDates = RangeUtils.intersection(
                        transHistory.getPerStatusDates((perStat) -> perStat.isEmployed() && perStat.isTimeEntryRequired()),
                        ImmutableRangeSet.of(yearRange));

                BigDecimal minHours = effectiveMinHours.lastEntry().getValue();
                BigDecimal dailyHours = AccrualUtils.getProratePercentage(minHours).multiply(new BigDecimal(7));
                long expectedDays = DateUtils.getNumberOfWeekdays(expectedDates);
                BigDecimal expTotalHrs = dailyHours.multiply(new BigDecimal(expectedDays));

                BigDecimal expectedExpectedHours = expTotalHrs.subtract(dockHours);

                ExpectedHours expectedHours = expHoursService.getExpectedHours(empId, Range.singleton(LocalDate.ofYearDay(2017, 1).minusDays(1)));
                BigDecimal actualExpHours = expectedHours.getYtdHoursExpected();
                assertEquals(expectedExpectedHours, actualExpHours);
            }
        }
    }
}
