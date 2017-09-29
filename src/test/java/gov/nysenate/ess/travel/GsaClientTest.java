package gov.nysenate.ess.travel;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.request.model.GsaClient;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class GsaClientTest {

    @Test(expected = IllegalArgumentException.class)
    public void incorrectFiscalYear() {
        GsaClient client = new GsaClient(0, "12110");
    }
}
