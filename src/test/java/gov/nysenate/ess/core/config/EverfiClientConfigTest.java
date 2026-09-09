package gov.nysenate.ess.core.config;

import gov.nysenate.ess.core.annotation.UnitTest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Category(UnitTest.class)
public class EverfiClientConfigTest {

    private final EverfiClientConfig config = new EverfiClientConfig();

    @Test
    public void httpTimeoutsMustBePositive() {
        assertThatThrownBy(() -> config.everfiHttpClient(0, 1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("everfi.http.connect-timeout-ms must be positive.");
        assertThatThrownBy(() -> config.everfiHttpClient(1, 0, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("everfi.http.connection-request-timeout-ms must be positive.");
        assertThatThrownBy(() -> config.everfiHttpClient(1, 1, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("everfi.http.socket-timeout-ms must be positive.");
    }

    @Test
    public void positiveHttpTimeoutsCreateClient() throws Exception {
        try (CloseableHttpClient ignored = config.everfiHttpClient(1, 1, 1)) {
            // Construction verifies that Apache HttpClient accepts the validated configuration.
        }
    }
}
