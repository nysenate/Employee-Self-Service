package gov.nysenate.ess.web.service.allowance;

import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.seta.model.allowances.AllowanceUsage;
import gov.nysenate.ess.seta.service.allowance.EssAllowanceService;
import gov.nysenate.ess.web.BaseTests;
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
