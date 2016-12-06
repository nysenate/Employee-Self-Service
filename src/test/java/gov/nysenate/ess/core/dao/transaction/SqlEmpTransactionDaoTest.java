package gov.nysenate.ess.core.dao.transaction;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.SillyTest;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.core.util.OutputUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Category(SillyTest.class)
public class SqlEmpTransactionDaoTest extends BaseTest
{
    private static final Logger logger = LoggerFactory.getLogger(SqlEmpTransactionDaoTest.class);

    @Autowired private SqlEmpTransactionDao empTransactionDao;

    @Test
    public void testGetTransHistory() throws Exception {
        logger.info("{}", OutputUtils.toJson(empTransactionDao.getTransHistory(10976, EmpTransDaoOption.INITIALIZE_AS_APP).getRecords(TransactionCode.SAL)));
    }

    @Test
    public void testGetTransHistory1() throws Exception {

    }

    @Test
    public void testGetTransHistory2() throws Exception {

    }

    @Test
    public void testCheckForUpdatesSince() throws Exception {
        logger.info("{}", empTransactionDao.getMaxUpdateDateTime());
    }
}