package gov.nysenate.ess.time.service.accrual;

import gov.nysenate.ess.core.dao.period.PayPeriodDao;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.core.BaseTests;
import gov.nysenate.ess.time.client.view.AccrualsView;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class SqlAccrualServiceTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlAccrualServiceTests.class);

    @Autowired
    private EssAccrualComputeService accService;

    @Autowired
    private PayPeriodDao payPeriodDao;

    @Test
    public void testGetAccruals() throws Exception {
        PayPeriod period = payPeriodDao.getPayPeriod(PayPeriodType.AF, LocalDate.of(2015, 9, 24));
        logger.info("{}", OutputUtils.toJson(
                new AccrualsView(accService.getAccruals(9560, period))));
    }
}
