package gov.nysenate.ess.time.service.accrual;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.service.period.PayPeriodService;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.time.model.accrual.PeriodAccSummary;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.TreeMap;

import static gov.nysenate.ess.core.model.period.PayPeriodType.AF;
import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class AccrualComputeServiceIT extends BaseTest {

    @Autowired private AccrualComputeService accrualComputeService;
    @Autowired private PayPeriodService payPeriodService;

    @Test
    public void getAccrualsSmokeTest() {
        int testEmp = 11423;
        int testYear = 2017;
        List<PayPeriod> payPeriods = payPeriodService.getPayPeriods(AF, DateUtils.yearDateRange(testYear), SortOrder.ASC);
        TreeMap<PayPeriod, PeriodAccSummary> accruals = accrualComputeService.getAccruals(testEmp, payPeriods);
        assertEquals(27, accruals.size());
    }
}
