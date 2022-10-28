package gov.nysenate.ess.time.service.accrual;

import gov.nysenate.ess.core.annotation.SillyTest;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.core.BaseTest;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.SortedSet;

@Category(SillyTest.class)
public class EssAccrualInfoServiceTest extends BaseTest
{
    private static final Logger logger = LoggerFactory.getLogger(EssAccrualInfoServiceTest.class);

    @Autowired private AccrualInfoService accrualInfoService;

    @Test
    public void testInit() throws Exception {

    }

    @Test
    public void testSetupCaches() throws Exception {

    }

    @Test
    public void testGetAnnualAccruals() throws Exception {
        StopWatch sw = new StopWatch();
        sw.start();
        accrualInfoService.getAnnualAccruals(10976, 2013);
        sw.stop();
        logger.info("{}", sw.getTime());
        sw.reset();
        sw.start();
        accrualInfoService.getAnnualAccruals(10976, 2015);
        sw.stop();
        logger.info("{}", sw.getTime());
        sw.reset();
    }

    @Test
    public void testOpenAttendancePeriods() throws Exception {
        StopWatch sw = new StopWatch();
        sw.start();
        accrualInfoService.getActiveAttendancePeriods(10976, LocalDate.now(), SortOrder.ASC);
        sw.stop();
        logger.info("{}", sw.getTime());
        sw.reset();sw.start();
        accrualInfoService.getActiveAttendancePeriods(10976, LocalDate.now(), SortOrder.ASC);
        sw.stop();
        logger.info("{}", sw.getTime());
        logger.info("{}", OutputUtils.toJson(accrualInfoService.getActiveAttendancePeriods(10976, LocalDate.now(), SortOrder.ASC)));
    }

    @Test
    public void getAccrualYearsTest() {
        int empId = 4117;

        SortedSet<Integer> accrualYears = accrualInfoService.getAccrualYears(empId);

        logger.info("{}", accrualYears);
    }
}