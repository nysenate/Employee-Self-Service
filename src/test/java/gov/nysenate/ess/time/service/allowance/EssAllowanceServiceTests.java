package gov.nysenate.ess.time.service.allowance;

import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.core.BaseTests;
import gov.nysenate.ess.time.model.allowances.AllowanceUsage;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EssAllowanceServiceTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(EssAllowanceServiceTests.class);

    @Autowired
    EssAllowanceService allowanceService;

    @Test
    public void getAllowanceTest() {
        AllowanceUsage usage = allowanceService.getAllowanceUsage(11303, 2015);
        logger.info("{}", OutputUtils.toJson(usage));
    }
}
