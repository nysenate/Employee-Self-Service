package gov.nysenate.ess.web.service.period;

import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.web.BaseTests;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class EssCachedPayPeriodServiceTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(EssCachedPayPeriodServiceTests.class);

    @Autowired private EssCachedPayPeriodService periodService;

    @Test
    public void getOpenPeriodsTest() {
        List<PayPeriod> openPayPeriods = periodService.getOpenPayPeriods(PayPeriodType.AF, 4856, SortOrder.ASC);
        logger.info("{}", openPayPeriods);
    }
}
