package gov.nysenate.ess.seta.dao.accrual;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.seta.SetaTests;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class SqlAccrualDaoTests extends SetaTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlAccrualDaoTests.class);

    @Autowired
    private SqlAccrualDao accDao;

    @Test
    public void testGetPeriodAccrualSummaries() throws Exception {
        logger.info("{}", OutputUtils.toJson(
                accDao.getPeriodAccruals(10976, LocalDate.now(), new LimitOffset(2), SortOrder.DESC)));
    }

    @Test
    public void testGetAnnualAccrualSummaries() throws Exception {
        logger.info("{}", OutputUtils.toJson(
            accDao.getAnnualAccruals(10976, 2015)
        ));
    }

    @Test
    public void testGetPeriodAccrualUsages() throws Exception {
        logger.info("{}", OutputUtils.toJson(
            accDao.getPeriodAccrualUsages(10976, Range.all())
        ));
    }

    @Test
    public void testGetAnnualAccUpdated() throws Exception {
        logger.info("{}", OutputUtils.toJson(accDao.getAnnualAccsUpdatedSince(LocalDateTime.of(2015, 9, 25, 0,0,0))));
    }
}
