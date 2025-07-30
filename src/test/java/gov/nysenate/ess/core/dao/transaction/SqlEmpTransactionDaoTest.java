package gov.nysenate.ess.core.dao.transaction;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.SillyTest;
import gov.nysenate.ess.core.model.transaction.TransactionRecord;
import gov.nysenate.ess.core.util.OutputUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static gov.nysenate.ess.core.model.transaction.TransactionCode.*;

@Category(SillyTest.class)
public class SqlEmpTransactionDaoTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(SqlEmpTransactionDaoTest.class);

    @Autowired private SqlEmpTransactionDao empTransactionDao;

    @Test
    public void testGetTransHistory() {
        logger.info("{}", OutputUtils.toJson(empTransactionDao.getTransHistory(10976, EmpTransDaoOption.INITIALIZE_AS_APP).getRecords(SAL)));
    }

    @Test
    public void testCheckForUpdatesSince() {
        logger.info("{}", empTransactionDao.getMaxUpdateDateTime());
    }

    @Test
    public void testCheckForPostedRecordsSince() {
        var codes = Set.of(APP, LOC, NAM, PHO, RTP, LIN, EMP);
        var sinceDateTime = LocalDateTime.now().minusDays(7);
        List<TransactionRecord> records = empTransactionDao.postedRecordsSince(sinceDateTime, codes);
        logger.info("{}", records.size());
    }
}