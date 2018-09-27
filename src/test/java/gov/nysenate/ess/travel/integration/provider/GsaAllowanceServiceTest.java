package gov.nysenate.ess.travel.integration.provider;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.travel.provider.gsa.GsaApi;
import gov.nysenate.ess.travel.provider.gsa.GsaCache;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@org.junit.experimental.categories.Category(IntegrationTest.class)
public class GsaAllowanceServiceTest extends BaseTest {

    @Autowired GsaCache gsaCache;
    @Autowired GsaApi gsaApi;

    @Test
    public void fetchesMealRate() {

    }
}
