package gov.nysenate.ess.seta.service.allowance;

import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.seta.SetaTests;
import gov.nysenate.ess.seta.model.allowances.AllowanceUsage;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EssAllowanceServiceTests extends SetaTests
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
