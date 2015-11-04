package gov.nysenate.ess.web.dao.transaction;

import gov.nysenate.ess.core.dao.transaction.EmpTransDaoOption;
import gov.nysenate.ess.core.dao.transaction.SqlEmpTransactionDao;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.web.BaseTests;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SqlEmpTransactionDaoTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlEmpTransactionDaoTests.class);

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