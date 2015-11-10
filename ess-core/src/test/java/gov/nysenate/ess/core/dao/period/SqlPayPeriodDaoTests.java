package gov.nysenate.ess.core.dao.period;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.DaoTests;
import gov.nysenate.ess.core.annotation.ProperTest;
import gov.nysenate.ess.core.annotation.TestDependsOnDatabase;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.core.util.SortOrder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category({ProperTest.class, TestDependsOnDatabase.class})
public class SqlPayPeriodDaoTests extends DaoTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlPayPeriodDaoTests.class);

    @Autowired private SqlPayPeriodDao payPeriodDao;

    /** Null date should yield an IllegalArgumentException */
    @Test(expected = IllegalArgumentException.class)
    public void testGetPayPeriod_nullDate() throws Exception {
        payPeriodDao.getPayPeriod(PayPeriodType.AF, null);
    }

    /** Null period type will yield an IllegalArgumentException */
    @Test(expected = IllegalArgumentException.class)
    public void testGetPayPeriod_nullPeriodType() throws Exception {
        payPeriodDao.getPayPeriod(null, LocalDate.now());
    }

    @Test
    public void testGetPayPeriod_validCases() throws Exception {
        // Pay period start date (jan 16, 2014)
        PayPeriod payPeriod = payPeriodDao.getPayPeriod(PayPeriodType.AF, LocalDate.of(2014, 1, 16));
        assertEquals(PayPeriodType.AF, payPeriod.getType());
        assertEquals(LocalDate.of(2014, 1, 16), payPeriod.getStartDate());
        assertEquals(LocalDate.of(2014, 1, 29), payPeriod.getEndDate());

        // Using pay period end date (feb 12, 2014)
        payPeriod = payPeriodDao.getPayPeriod(PayPeriodType.AF, LocalDate.of(2014, 2, 12));
        assertEquals(LocalDate.of(2014, 1, 30), payPeriod.getStartDate());
        assertEquals(LocalDate.of(2014, 2, 12), payPeriod.getEndDate());

        // Some date in between (feb 5, 2014)
        payPeriod = payPeriodDao.getPayPeriod(PayPeriodType.AF, LocalDate.of(2014, 2, 5));
        assertEquals(LocalDate.of(2014, 1, 30), payPeriod.getStartDate());
        assertEquals(LocalDate.of(2014, 2, 12), payPeriod.getEndDate());

        // Split pay period start (jan 1, 2014)
        payPeriod = payPeriodDao.getPayPeriod(PayPeriodType.AF, LocalDate.of(2014, 1, 1));
        assertEquals(LocalDate.of(2014, 1, 1), payPeriod.getStartDate());
        assertEquals(LocalDate.of(2014, 1, 1), payPeriod.getEndDate());

        // Split pay period end (dec 31, 2013)
        payPeriod = payPeriodDao.getPayPeriod(PayPeriodType.AF, LocalDate.of(2013, 12, 31));
        assertEquals(LocalDate.of(2013, 12, 19), payPeriod.getStartDate());
        assertEquals(LocalDate.of(2013, 12, 31), payPeriod.getEndDate());
    }

    /** Ensure that all pay period types return valid periods for AF, PF, and SF. Other period types
     *  aren't widely used. */
    @Test
    public void testGetPayPeriod_allPeriodTypes() throws Exception {
        LocalDate date = LocalDate.of(2014, 1, 14); // Use a sample date
        Stream.of(new PayPeriodType[]{PayPeriodType.AF, PayPeriodType.PF, PayPeriodType.SF}).forEach(type -> {
            assertEquals(type, payPeriodDao.getPayPeriod(type, date).getType());
        });
    }

    /** Check that the correct numDaysIsPeriod is being set. */
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

    /** Pay periods that occur during the Daylight savings transition should report 14 days as usual. */
    @Test
    public void testGetPayPeriodDays_checkForDaylightSavingsIssues() throws Exception {
        PayPeriod marchDSTPeriod = payPeriodDao.getPayPeriod(PayPeriodType.AF, LocalDate.of(2014, 3, 12));
        PayPeriod novemberDSTPeriod = payPeriodDao.getPayPeriod(PayPeriodType.AF, LocalDate.of(2015, 11, 1));

        assertEquals(14, marchDSTPeriod.getNumDaysInPeriod());
        assertEquals(14, novemberDSTPeriod.getNumDaysInPeriod());
    }

    @Test
    public void testGetPayPeriods_range() throws Exception {
        // 2014 had a start of year split so should have 27 pay periods
        Range<LocalDate> closedOneYear = Range.closed(LocalDate.of(2014, 1, 1), LocalDate.of(2014, 12, 31));
        List<PayPeriod> payPeriods = payPeriodDao.getPayPeriods(PayPeriodType.AF, closedOneYear, SortOrder.ASC);
        assertEquals(27, payPeriods.size());
        assertEquals(LocalDate.of(2014, 1, 1), payPeriods.get(0).getStartDate());
        assertEquals(LocalDate.of(2014, 12, 31), payPeriods.get(26).getEndDate());

        // 2015 before the end of year split should be 26 periods
        Range<LocalDate> closedOpenYear = Range.closedOpen(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 12, 30));
        payPeriods = payPeriodDao.getPayPeriods(PayPeriodType.AF, closedOpenYear, SortOrder.ASC);
        assertEquals(26, payPeriods.size());
        assertEquals(LocalDate.of(2015, 1, 1), payPeriods.get(0).getStartDate());
        assertEquals(LocalDate.of(2015, 12, 30), payPeriods.get(25).getEndDate());

        // Test multiple years span
        Range<LocalDate> openMultiYears = Range.open(LocalDate.of(2014, 11, 19), LocalDate.of(2015, 2, 12));
        payPeriods = payPeriodDao.getPayPeriods(PayPeriodType.AF, openMultiYears, SortOrder.ASC);
        assertEquals(LocalDate.of(2014, 11, 20), payPeriods.get(0).getStartDate());
        assertEquals(LocalDate.of(2015, 2, 11), payPeriods.get(payPeriods.size() - 1).getEndDate());

        // Test the sort order
        payPeriods = payPeriodDao.getPayPeriods(PayPeriodType.AF, openMultiYears, SortOrder.ASC);
        for (int i = payPeriods.size() - 1; i >= 1; i--) {
            // Periods are in increasing order
            assertTrue(payPeriods.get(i).getStartDate().isAfter(payPeriods.get(i - 1).getEndDate()));
        }
        payPeriods = payPeriodDao.getPayPeriods(PayPeriodType.AF, openMultiYears, SortOrder.DESC);
        for (int i = payPeriods.size() - 1; i >= 1; i--) {
            // Periods are in decreasing order
            assertTrue(payPeriods.get(i).getEndDate().isBefore(payPeriods.get(i - 1).getStartDate()));
        }
    }
}