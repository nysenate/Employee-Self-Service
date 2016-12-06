package gov.nysenate.ess.time.service.accrual;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.annotation.SillyTest;
import gov.nysenate.ess.core.dao.period.PayPeriodDao;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.core.BaseTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

@Category(SillyTest.class)
public class EssAccrualComputeServiceTest extends BaseTest
{
    private static final Logger logger = LoggerFactory.getLogger(EssAccrualComputeServiceTest.class);

    @Autowired
    PayPeriodDao payPeriodDao;
    @Autowired
    EssAccrualComputeService accrualComputeService;

    @Test
    public void testGetAccruals() throws Exception {
        logger.info("{}", OutputUtils.toJson(
            accrualComputeService.getAccruals(10976, payPeriodDao.getPayPeriod(PayPeriodType.AF, LocalDate.now()))));
    }

    @Test
    public void testGetAccruals1() throws Exception {
        List<PayPeriod> payPeriods = payPeriodDao.getPayPeriods(
            PayPeriodType.AF, Range.closed(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 8, 1)), SortOrder.ASC);
        logger.info("{}", OutputUtils.toJson(accrualComputeService.getAccruals(10976, payPeriods)));
    }
}