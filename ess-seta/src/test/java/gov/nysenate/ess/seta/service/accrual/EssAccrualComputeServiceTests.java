package gov.nysenate.ess.seta.service.accrual;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.dao.period.PayPeriodDao;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.seta.SetaTests;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

public class EssAccrualComputeServiceTests extends SetaTests
{
    private static final Logger logger = LoggerFactory.getLogger(EssAccrualComputeServiceTests.class);

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