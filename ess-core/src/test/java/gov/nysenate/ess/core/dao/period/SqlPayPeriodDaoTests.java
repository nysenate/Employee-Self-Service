package gov.nysenate.ess.core.dao.period;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.CoreTests;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.core.util.SortOrder;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

public class SqlPayPeriodDaoTests extends CoreTests
{

    private static final Logger logger = LoggerFactory.getLogger(SqlPayPeriodDaoTests.class);

    @Autowired
    private PayPeriodDao payPeriodDao;

    @Test
    public void testGetPayPeriod() throws Exception {
        logger.info(OutputUtils.toJson(payPeriodDao.getPayPeriod(PayPeriodType.AF, LocalDate.of(2012, 11, 7))));
        StopWatch sw = new StopWatch();
        sw.start();
        payPeriodDao.getPayPeriod(PayPeriodType.AF, LocalDate.of(2012, 11, 7));
        payPeriodDao.getPayPeriod(PayPeriodType.AF, LocalDate.of(2012, 11, 7));
        payPeriodDao.getPayPeriod(PayPeriodType.AF, LocalDate.of(2012, 11, 7));
        sw.stop();
        logger.info("{}", sw.getTime());
    }

    @Test
    public void testGetPayPeriods() throws Exception {
        logger.info(OutputUtils.toJson(payPeriodDao.getPayPeriods(PayPeriodType.AF,
                Range.closed(LocalDate.of(2010, 9, 2), LocalDate.of(2010, 10, 13)), SortOrder.ASC)));
    }

    @Test
    public void testGetOpenPayPeriods() throws Exception {
//        logger.info(OutputUtils.toJson(payPeriodDao.getOpenAttendancePayPeriods(10976, LocalDate.now(), SortOrder.DESC)));
    }

    @Test
    public void testGetPayPeriodDays() throws Exception {
        /** Regular pay period */
        PayPeriod period = payPeriodDao.getPayPeriod(PayPeriodType.AF, LocalDate.of(2014, 4, 23));
        Assert.assertEquals(14, period.getNumDaysInPeriod());

        /** Split after new year */
        period = payPeriodDao.getPayPeriod(PayPeriodType.AF, LocalDate.of(2014, 1, 1));
        Assert.assertEquals(1, period.getNumDaysInPeriod());

        /** Split before new year */
        period = payPeriodDao.getPayPeriod(PayPeriodType.AF, LocalDate.of(2013, 12, 31));
        Assert.assertEquals(13, period.getNumDaysInPeriod());
    }

    @Test
    public void testGetPayPeriodDays_checkForDaylightSavingsIssues() throws Exception {
        PayPeriod marchDSTPeriod = payPeriodDao.getPayPeriod(PayPeriodType.AF, LocalDate.of(2014, 3, 12));
        PayPeriod novemberDSTPeriod = payPeriodDao.getPayPeriod(PayPeriodType.AF, LocalDate.of(2014, 11, 5));
        Assert.assertEquals(14, marchDSTPeriod.getNumDaysInPeriod());
        Assert.assertEquals(14, novemberDSTPeriod.getNumDaysInPeriod());
    }

    @Test
    public void testOpenPayPeriods() throws Exception {
//        logger.info("{}", payPeriodDao.getOpenAttendancePayPeriods(9896, LocalDate.now(), SortOrder.ASC));
    }
}