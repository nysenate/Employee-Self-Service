package gov.nysenate.ess.core.service.pec.external.everfi.user;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiClient;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class EverfiUsersRequestTest {

    private final EverfiApiClient everfiApiClient = new EverfiApiClient("https://example.com", null, null);

    @Test(expected = IllegalArgumentException.class)
    public void invalidPageSizeThrows() {
        new EverfiUsersRequest(everfiApiClient, 1, EverfiUsersRequest.MAX_PAGE_SIZE + 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidPageThrows() {
        new EverfiUsersRequest(everfiApiClient, 0, EverfiUsersRequest.DEFAULT_PAGE_SIZE);
    }
}
