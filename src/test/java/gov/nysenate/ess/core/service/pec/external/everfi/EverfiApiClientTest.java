package gov.nysenate.ess.core.service.pec.external.everfi;

import gov.nysenate.ess.core.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class EverfiApiClientTest {

    @Test
    public void rateLimitBackoffStopsAtConfiguredWaitLimit() throws Exception {
        EverfiApiClient client = new EverfiApiClient("https://example.com", null, null, 1_000);

        EverfiApiException exception = assertThrows(EverfiApiException.class,
                () -> client.rateLimitWaitMillis(2, 400));

        assertEquals(429, exception.getStatusCode());
        assertEquals("Exceeded maximum rate-limit wait of 1000 ms.", exception.getResponseBody());
    }

    @Test
    public void rateLimitBackoffReturnsNextDelayWithinLimit() throws Exception {
        EverfiApiClient client = new EverfiApiClient("https://example.com", null, null, 1_200);

        assertEquals(800, client.rateLimitWaitMillis(2, 400));
    }

    @Test
    public void acceptsAnySuccessfulHttpStatus() {
        assertTrue(EverfiApiClient.isSuccessStatus(200));
        assertTrue(EverfiApiClient.isSuccessStatus(204));
        assertTrue(EverfiApiClient.isSuccessStatus(299));
        assertFalse(EverfiApiClient.isSuccessStatus(300));
    }

    @Test
    public void rejectsNegativeRateLimitWait() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new EverfiApiClient("https://example.com", null, null, -1));

        assertEquals("everfi.http.max-rate-limit-wait-ms must not be negative.", exception.getMessage());
    }
}
