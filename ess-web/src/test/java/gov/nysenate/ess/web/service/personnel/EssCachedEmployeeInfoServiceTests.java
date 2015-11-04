package gov.nysenate.ess.web.service.personnel;

import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.web.BaseTests;
import gov.nysenate.ess.core.annotation.ProperTest;
import gov.nysenate.ess.core.annotation.TestDependsOnDatabase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@ProperTest
@TestDependsOnDatabase
public class EssCachedEmployeeInfoServiceTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(EssCachedEmployeeInfoServiceTests.class);

    @Autowired
    EmployeeInfoService employeeInfoService;

    @Test
    public void testGetEmployeeActiveDatesService_activeEmployee() throws Exception {
//        logger.info("{}", employeeInfoService.getEmployeeActiveDatesService(10976));
   }
}