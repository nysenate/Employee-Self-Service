package gov.nysenate.ess.core.dao.period;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.CoreTests;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.core.util.SortOrder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

public class SqlHolidayDaoTests extends CoreTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlHolidayDaoTests.class);

    @Autowired
    protected SqlHolidayDao holidayDao;

    @Test
    public void testGetHoliday() throws Exception {
        logger.info(OutputUtils.toJson(holidayDao.getHoliday(LocalDate.of(2014, 1, 1))));
    }

    @Test
    public void testGetHolidays() throws Exception {
        logger.info(OutputUtils.toJson(holidayDao.getHolidays(
                Range.closed(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 12, 31)), true, SortOrder.DESC)));
    }
}