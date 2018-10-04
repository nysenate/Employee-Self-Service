package gov.nysenate.ess.time.service.payroll;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.annotation.TestDependsOnDatabase;
import gov.nysenate.ess.time.model.payroll.Paycheck;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

@Category({IntegrationTest.class, TestDependsOnDatabase.class})
public class PaycheckServiceIT extends BaseTest
{
    private static final Logger logger = LoggerFactory.getLogger(PaycheckServiceIT.class);

    @Autowired
    PaycheckService paycheckService;
    private final int empId = 11168;
    private final int year = 2018;
    private List<Paycheck> paychecks;

    @Before
    public void before() {
        paychecks = paycheckService.getEmployeePaychecksForYear(empId, year);
    }

    @Test
    public void serviceCorrectlyInitializesPaycheck() {
        assertTrue(paychecks.size() >= 1);
        for (Paycheck paycheck : paychecks) {
            assertThat(paycheck.getCheckDate().getYear(), is(year));
            assertTrue(paycheck.getPayPeriod().matches("[0-9]{1,2}")); //
            assertTrue(paycheck.getGrossIncome() != null);
            assertTrue(paycheck.getNetIncome() != null);
            assertTrue(paycheck.getDeductions() != null);
            assertTrue(paycheck.getDirectDepositAmount() != null);
            assertTrue(paycheck.getCheckAmount() != null);
        }
    }

    @Test
    public void sumOfDirectDepositsAndCheckShouldEqualNetIncome() {
        Paycheck paycheck = paychecks.get(0);
        assertThat(paycheck.getDirectDepositAmount().add(paycheck.getCheckAmount()), is(paycheck.getNetIncome()));
    }

    @Test
    public void grossIncomeMinusDeductionsShouldEqualNetIncome() {
        Paycheck paycheck = paychecks.get(0);
        BigDecimal calculatedNet = paycheck.getGrossIncome().subtract(paycheck.getTotalDeductions());
        assertTrue("Gross Income: " + paycheck.getGrossIncome() +
                        " - " + paycheck.getTotalDeductions() +
                        " != " + paycheck.getNetIncome() +
                        "\tGot: " + calculatedNet,
                   calculatedNet.compareTo(paycheck.getNetIncome()) == 0);
    }
}
