package gov.nysenate.ess.core.service.pec.external.everfi;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.ess.core.annotation.UnitTest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.HttpContext;
import org.apache.http.params.HttpParams;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class EverfiClientTokenProviderTest {

    @Test
    public void standardExpiresInResponseIsCachedUntilRefreshSkew() throws Exception {
        MutableClock clock = new MutableClock(Instant.parse("2026-01-01T00:00:00Z"));
        RecordingHttpClient http = new RecordingHttpClient(
                tokenResponse("first-token", clock.instant(), 3_600),
                tokenResponse("second-token", clock.instant(), 3_600));
        EverfiClientTokenProvider provider = new EverfiClientTokenProvider(
                http,
                new ObjectMapper(),
                "client-id",
                "client-secret",
                "https://auth.example.test/oauth/token",
                clock,
                Duration.ofMinutes(1));

        assertThat(provider.getAccessToken()).isEqualTo("first-token");
        clock.advance(Duration.ofSeconds(3_539));
        assertThat(provider.getAccessToken()).isEqualTo("first-token");
        clock.advance(Duration.ofSeconds(1));
        assertThat(provider.getAccessToken()).isEqualTo("second-token");

        assertThat(http.executeCalls).isEqualTo(2);
    }

    private CloseableHttpResponse tokenResponse(String token, Instant createdAt, long expiresIn) {
        TestResponse response = new TestResponse();
        response.setEntity(new StringEntity(
                "{\"access_token\":\"" + token + "\",\"created_at\":"
                        + createdAt.getEpochSecond() + ",\"expires_in\":" + expiresIn + "}",
                ContentType.APPLICATION_JSON));
        return response;
    }

    @SuppressWarnings("deprecation")
    private static class RecordingHttpClient extends CloseableHttpClient {
        private final Deque<CloseableHttpResponse> responses;
        private int executeCalls;

        private RecordingHttpClient(CloseableHttpResponse... responses) {
            this.responses = new ArrayDeque<>(Arrays.asList(responses));
        }

        @Override
        protected CloseableHttpResponse doExecute(HttpHost target, HttpRequest request, HttpContext context) {
            executeCalls++;
            return responses.removeFirst();
        }

        @Override
        public void close() {
        }

        @Override
        public HttpParams getParams() {
            return null;
        }

        @Override
        public ClientConnectionManager getConnectionManager() {
            return null;
        }
    }

    private static class TestResponse extends BasicHttpResponse implements CloseableHttpResponse {
        private TestResponse() {
            super(new BasicStatusLine(
                    new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_OK, "OK"));
        }

        @Override
        public void close() {
        }
    }

    private static class MutableClock extends Clock {
        private Instant instant;

        private MutableClock(Instant instant) {
            this.instant = instant;
        }

        private void advance(Duration duration) {
            instant = instant.plus(duration);
        }

        @Override
        public ZoneId getZone() {
            return ZoneId.of("UTC");
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }
}
