package gov.nysenate.ess.time.service.accrual;

import gov.nysenate.ess.core.annotation.SillyTest;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.core.BaseTests;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@Category(SillyTest.class)
public class EssCachedAccrualInfoServiceTest extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(EssCachedAccrualInfoServiceTest.class);

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
}