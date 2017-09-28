package gov.nysenate.ess.travel;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.request.model.GSAClient;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class GSAClientTest {

    @Test
    public void something() {
        GSAClient client = new GSAClient(2017, 12110);
        assertEquals(client.getRecords().get("Meals").getAsString(), "59");
    }
}
