package gov.nysenate.ess.travel.integration.allowances;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.travel.allowance.gsa.service.GsaClient;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class GsaClientIT extends BaseTest {

    @Autowired GsaClient client;

//    @Test(expected = IllegalArgumentException.class)
//    public void incorrectFiscalYear() {
//        client.get(0, "12110");
//    }

    @Test
    public void correctFiscalYear() throws IOException {
        client.queryGsa(LocalDate.now(), "10036");
    }
}
