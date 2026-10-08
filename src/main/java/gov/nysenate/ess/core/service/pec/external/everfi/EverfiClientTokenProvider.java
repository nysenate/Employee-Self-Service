package gov.nysenate.ess.core.service.pec.external.everfi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class EverfiClientTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(EverfiClientTokenProvider.class);

    private final CloseableHttpClient http;
    private final ObjectMapper objectMapper;

    private final String clientId;
    private final String clientSecret;
    private final URI tokenUri;

    private final Clock clock;
    private final Duration refreshSkew;

    private volatile CachedToken cached; // fast path reads
    private final ReentrantLock lock = new ReentrantLock();

    public EverfiClientTokenProvider(
            CloseableHttpClient http,
            ObjectMapper objectMapper,
            String clientId,
            String clientSecret,
            String tokenUrl,
            Clock clock,
            Duration refreshSkew
    ) {
        this.http = Objects.requireNonNull(http);
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.clientId = Objects.requireNonNull(clientId);
        this.clientSecret = Objects.requireNonNull(clientSecret);
        this.tokenUri = requireHttpsUri(tokenUrl);
        this.clock = Objects.requireNonNull(clock);
        this.refreshSkew = Objects.requireNonNull(refreshSkew);
        if (refreshSkew.isNegative()) {
            throw new IllegalArgumentException("Everfi token refresh skew must not be negative.");
        }
    }

    public String getAccessToken() throws IOException {
        CachedToken t = cached;
        if (t != null && !t.isExpiringSoon(clock.instant(), refreshSkew)) {
            return t.accessToken;
        }

        lock.lock();
        try {
            // re-check after acquiring lock
            t = cached;
            if (t != null && !t.isExpiringSoon(clock.instant(), refreshSkew)) {
                return t.accessToken;
            }

            CachedToken newToken = fetchNewToken();
            cached = newToken;
            return newToken.accessToken;
        } finally {
            lock.unlock();
        }
    }

    public void invalidate() {
        cached = null;
    }

    private CachedToken fetchNewToken() throws IOException {
        HttpPost post = new HttpPost(tokenUri);

        post.setEntity(new StringEntity("grant_type=client_credentials", ContentType.APPLICATION_FORM_URLENCODED));
        post.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        // Basic auth for client_id/client_secret (common + recommended)
        String basic = Base64.getEncoder().encodeToString(
                (clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8)
        );
        post.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + basic);

        long startNanos = System.nanoTime();
        logger.debug("Starting Everfi OAuth token request.");
        try (CloseableHttpResponse resp = http.execute(post)) {
            int statusCode = resp.getStatusLine().getStatusCode();
            String body = resp.getEntity() == null ? "" : EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);

            logger.debug("Finished Everfi OAuth token request with HTTP {} in {} ms.",
                    statusCode, (System.nanoTime() - startNanos) / 1_000_000);

            if (statusCode < 200 || statusCode >= 300) {
                logger.error("Error authenticating with Everfi.");
                throw new IOException("Token endpoint failed: HTTP " + statusCode + " body=" + body);
            }

            OAuthTokenResponse tr = objectMapper.readValue(body, OAuthTokenResponse.class);

            if (tr.accessToken == null || tr.accessToken.isEmpty()) {
                throw new IOException("Token endpoint returned no access_token. body=" + body);
            }

            Instant expiresAt = tokenExpiration(tr);
            return new CachedToken(tr.accessToken, expiresAt);
        } catch (IOException | RuntimeException ex) {
            logger.error("Everfi OAuth token request failed after {} ms.",
                    (System.nanoTime() - startNanos) / 1_000_000, ex);
            throw ex;
        }
    }

    private Instant tokenExpiration(OAuthTokenResponse response) throws IOException {
        try {
            if (response.expiresAt > 0) {
                return Instant.ofEpochSecond(response.expiresAt);
            }
            if (response.expiresIn <= 0) {
                throw new IOException("Token endpoint returned no valid expiration.");
            }

            Instant issuedAt = response.createdAt > 0
                    ? Instant.ofEpochSecond(response.createdAt)
                    : clock.instant();
            return issuedAt.plusSeconds(response.expiresIn);
        } catch (DateTimeException | ArithmeticException ex) {
            throw new IOException("Token endpoint returned an invalid expiration.", ex);
        }
    }

    private static URI requireHttpsUri(String tokenUrl) {
        URI uri = URI.create(Objects.requireNonNull(tokenUrl));
        if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null) {
            throw new IllegalArgumentException("Everfi OAuth token URL must be an absolute HTTPS URL.");
        }
        return uri;
    }

    private static final class CachedToken {
        final String accessToken;
        final Instant expiresAt;

        CachedToken(String accessToken, Instant expiresAt) {
            this.accessToken = accessToken;
            this.expiresAt = expiresAt;
        }

        boolean isExpiringSoon(Instant now, Duration skew) {
            // refresh early: if now + skew >= expiresAt
            return expiresAt == null || !now.plus(skew).isBefore(expiresAt);
        }
    }
}
