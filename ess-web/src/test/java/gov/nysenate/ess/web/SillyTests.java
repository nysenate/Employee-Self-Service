package gov.nysenate.ess.web;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.core.service.mail.SendMailService;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.web.service.period.PayPeriodService;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.transaction.EmpTransactionService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A sample file to run misc tests.
 */
public class SillyTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SillyTests.class);

    @Autowired
    PayPeriodService payPeriodService;

    @Autowired
    EmpTransactionService transactionService;

    @Autowired EmployeeInfoService employeeInfoService;
    @Autowired
    EmployeeDao employeeDao;

    @Autowired
    SendMailService sendMailService;

    @Autowired
    Environment env;

    @Test
    public void testEnvironment() throws Exception {
//        logger.info("{}", env.getActiveProfiles());
        logger.info("{}", env.acceptsProfiles("!test"));
//        logger.info("{}", env.acceptsProfiles("prod"));
//        logger.info("{}", env.acceptsProfiles("test"));
    }

    @Test
    public void testSendMail() throws Exception {
        sendMailService.sendMessage("islam@nysenate.gov", "Send your timesheets in!", "Please send in your timesheets now!");
    }

    @Test
    public void testGetActiveEmps() throws Exception {
        logger.info("{}", employeeDao.getActiveEmployeeIds());
    }

    @Test
    public void testEncloses() throws Exception {
        Range<LocalDate> enclosingRange = Range.openClosed(LocalDate.of(2015, 9, 1), LocalDate.of(2015, 9, 23));

        logger.info("{}", enclosingRange.encloses(payPeriodService.getPayPeriod(PayPeriodType.AF, LocalDate.now()).getDateRange()));
    }

    @Test
    public void testName() throws Exception {
        logger.info("{}", transactionService.getTransHistory(100).getEffectiveEmpStatus(DateUtils.ALL_DATES));
        logger.info("{}", employeeInfoService.getEmployeeActiveDatesService(100));
    }

    interface Counter
    {
        void increment();
        long get();
    }

    static class CounterClient implements Runnable
    {
        private Counter c;
        private int num;

        public CounterClient(Counter c, int num) {
            this.c = c;
            this.num = num;
        }

        @Override
        public void run() {
            for (int i = 0; i < num; i++) {
                c.increment();
            }
        }
    }

    static class StupidCounter implements Counter {
        private volatile AtomicLong counter = new AtomicLong(0);

        @Override
        public void increment() {
            counter.incrementAndGet();
        }

        @Override
        public long get() {
            return counter.get();
        }
    }

    @Test
    public void testMadThreads() throws Exception {
        int NUM_OF_THREADS = 1000;
        int NUM_OF_INCREMENTS = 1000000;
        ExecutorService service = Executors.newFixedThreadPool(40);
        Counter counter = new StupidCounter();
        long before = System.currentTimeMillis();
        for (int i = 0; i < NUM_OF_THREADS; i++) {
            service.submit(new CounterClient(counter, NUM_OF_INCREMENTS));
        }
        service.shutdown();
        service.awaitTermination(1, TimeUnit.MINUTES);
        long after = System.currentTimeMillis();
        System.out.println("Counter result: " + counter.get());
        System.out.println("Time passed in ms:" + (after - before));

    }
}