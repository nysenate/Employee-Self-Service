package gov.nysenate.ess.web.model.transaction;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.web.BaseTests;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.core.dao.transaction.EmpTransactionDao;
import gov.nysenate.ess.core.service.transaction.EmpTransactionService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.TreeSet;

import static gov.nysenate.ess.core.dao.transaction.EmpTransDaoOption.NONE;

public class TransactionHistoryTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(TransactionHistoryTests.class);

    @Autowired private EmpTransactionDao transactionDao;
    @Autowired private EmpTransactionService transService;
    @Autowired private EmployeeDao empDao;

    @Test
    public void testHasRecords() throws Exception {
//        logger.info("{}", OutputUtils.toJson(transactionDao.getTransHistory(10976, NONE).getAllTransRecords(SortOrder.ASC)));
        logger.info("{}", OutputUtils.toJson(transactionDao.getTransHistory(6221, NONE).getEffectiveSupervisorIds(
            Range.atMost(LocalDate.now()))));
    }

    @Test
    public void testAddTransactionRecord() throws Exception {

    }

    @Test
    public void testAddTransactionRecords() throws Exception {

    }

    @Test
    public void testGetTransRecords() throws Exception {

    }

    @Test
    public void testGetTransRecords1() throws Exception {

    }

    @Test
    public void testGetAllTransRecords() throws Exception {

    }

    @Test
    public void isFullyAppointedTest() {
        TreeSet<Integer> activeEmpIds = new TreeSet<>(
                empDao.getActiveEmployeeIds());
        for (int empId : activeEmpIds) {
            TransactionHistory transHistory = transService.getTransHistory(empId);
            if (!transHistory.isFullyAppointed()) {
                logger.info("{} is not fully appointed !!!", empId);
            }
        }
    }

//    @Test
//    public void getHourlyPaymentsTest() {
//        TransactionHistory transHistory = transactionDao.getTransHistory(10683, NONE);
//        int year = 2013;
//        List<HourlyWorkPayment> hourlyPayments = transHistory.getHourlyPayments(year);
//        hourlyPayments.forEach(hp -> logger.info("dte:{}\thrs:{}\tmoney:{}\tthisyr:{}\tlastyr:{}\tnxtYr:{}",
//                hp.getEffectDate(), hp.getHoursPaid(), hp.getMoneyPaid(),
//                hp.getMoneyPaidForYear(year), hp.getMoneyPaidForYear(year-1), hp.getMoneyPaidForYear(year + 1)));
//    }
//
//    @Test
//    public void getSalariesTest() {
//        TransactionHistory transHistory = transactionDao.getTransHistory(4856, NONE);
//        RangeMap<LocalDate, SalaryRec> salaryRecs = transHistory.getSalaryRecs();
//        salaryRecs.asMapOfRanges().values().forEach(salaryRec -> logger.info("{}", OutputUtils.toJson(salaryRec)));
//    }
}
