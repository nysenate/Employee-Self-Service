package gov.nysenate.ess.core.service.pec;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.service.pec.external.PECVideoCSVService;
import gov.nysenate.ess.web.SillyTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(SillyTest.class)
public class VideoTaskCSVServiceTest extends BaseTest {

    @Autowired private PECVideoCSVService csvService;

    @Test
    public void csvServiceTest() throws Exception {
        csvService.processCSVReports();
    }
}