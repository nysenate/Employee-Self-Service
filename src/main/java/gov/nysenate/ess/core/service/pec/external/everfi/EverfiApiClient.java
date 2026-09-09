package gov.nysenate.ess.core.service.pec.external.everfi;

import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;


/**
 * This class is responsible for making API requests to the Everfi API.
 * <p>
 * It will automatically re-authenticate with Everfi as necessary.
 */
@Service
public class EverfiApiClient {

    private static final Logger logger = LoggerFactory.getLogger(EverfiApiClient.class);

    private final String host;
    private final CloseableHttpClient everfiHttpClient;
    private final EverfiClientTokenProvider tokenProvider;

    private static final int SUCCESS_STATUS_MIN = 200;
    private static final int SUCCESS_STATUS_MAX = 299;
    private static final int NOT_FOUND = 404;
    private static final int EXPIRED_TOKEN_CODE = 401;
    private static final int RATE_LIMIT_EXCEEDED = 429;
    private static final int MAX_RETRIES = 10;
    private final long maxRateLimitWaitMs;

    public EverfiApiClient(
            @Value("${pec.everfi.host}") String host,
            CloseableHttpClient everfiHttpClient,
            EverfiClientTokenProvider tokenProvider,
            @Value("${everfi.http.max-rate-limit-wait-ms:60000}") long maxRateLimitWaitMs) {
        if (maxRateLimitWaitMs < 0) {
            throw new IllegalArgumentException("everfi.http.max-rate-limit-wait-ms must not be negative.");
        }
        this.host = host;
        this.everfiHttpClient = everfiHttpClient;
        this.tokenProvider = tokenProvider;
        this.maxRateLimitWaitMs = maxRateLimitWaitMs;
    }

    /**
     * Makes a GET request to the given Everfi API endpoint.
     *
     * @param endpoint The endpoint of the API to call. Including any query parameters necessary.
     * @return The body of the response as a String.
     * @throws IOException If there is an error making the request.
     */
    public String get(String endpoint) throws IOException {
        return makeRequest(new HttpGet(host + endpoint), null);
    }

    /**
     * Makes a POST request to the given Everfi API endpoint with the given body entity.
     *
     * @param endpoint The endpoint of the API to call. Including any query parameters necessary.
     * @param body     The body of the post request to be sent.
     * @return The body of the response as a String.
     * @throws IOException If there is an error making the request.
     */
    public String post(String endpoint, String body) throws IOException {
        return makeRequest(new HttpPost(host + endpoint), body);
    }

    /**
     * Makes a PATCH request to the given Everfi API endpoint with the given body entity.
     *
     * @param endpoint The endpoint of the API to call. Including any query parameters necessary.
     * @param body     The body of the post request to be sent.
     * @return The body of the response as a String.
     * @throws IOException If there is an error making the request.
     */
    public String patch(String endpoint, String body) throws IOException {
        return makeRequest(new HttpPatch(host + endpoint), body);
    }

    private String makeRequest(HttpUriRequest req, String entity) throws IOException {
        if (req instanceof HttpPost) {
            ((HttpPost) req).setEntity(new StringEntity(entity));
        } else if (req instanceof HttpPatch) {
            ((HttpPatch) req).setEntity(new StringEntity(entity));
        }

        ResponseResult result = executeOnce(req);
        if (result.isSuccess()) {
            return result.body();
        }

        // Retry once on an expired token status code.
        if (result.statusCode() == EXPIRED_TOKEN_CODE) {
            tokenProvider.invalidate();
            result = executeOnce(req);
            if (result.isSuccess()) {
                return result.body();
            }
        }

        // Retry up to MAX_RETRIES on rate limit exceeded status code.
        if (result.statusCode() == RATE_LIMIT_EXCEEDED) {
            return retryRateLimited(req);
        }

        throw failure(result);
    }

    private ResponseResult executeOnce(HttpUriRequest req) throws IOException {
        String target = requestTarget(req);
        long startNanos = System.nanoTime();
        logRequestStarted(req, target);
        try {
            updateHeaders(req);
            try (CloseableHttpResponse response = everfiHttpClient.execute(req)) {
                int status = response.getStatusLine().getStatusCode();
                String body = response.getEntity() == null ? null : EntityUtils.toString(response.getEntity());
                logRequestFinished(req, target, status, elapsedMillis(startNanos));
                return new ResponseResult(status, body);
            }
        } catch (IOException | RuntimeException ex) {
            logger.error("Everfi API request failed: {} after {} ms.",
                    target, elapsedMillis(startNanos), ex);
            throw ex;
        }
    }

    private long elapsedMillis(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000;
    }

    private void logRequestStarted(HttpUriRequest req, String target) {
        if (modifiesEverfiData(req)) {
            logger.info("Starting Everfi API request: {}.", target);
        } else {
            logger.debug("Starting Everfi API request: {}.", target);
        }
    }

    private void logRequestFinished(HttpUriRequest req, String target, int status, long durationMs) {
        if (modifiesEverfiData(req)) {
            logger.info("Finished Everfi API request: {} returned HTTP {} in {} ms.",
                    target, status, durationMs);
        } else {
            logger.debug("Finished Everfi API request: {} returned HTTP {} in {} ms.",
                    target, status, durationMs);
        }
    }

    private boolean modifiesEverfiData(HttpUriRequest req) {
        return switch (req.getMethod()) {
            case "POST", "PUT", "PATCH", "DELETE" -> true;
            default -> false;
        };
    }

    private String retryRateLimited(HttpUriRequest req) throws IOException {
        long totalWaitMs = 0;
        for (int retry = 1; retry <= MAX_RETRIES; retry++) {
            long waitMs;
            try {
                waitMs = rateLimitWaitMillis(retry, totalWaitMs);
            } catch (EverfiApiException ex) {
                logger.error("Aborting Everfi rate-limit retries for {} after {} ms of backoff.",
                        requestTarget(req), totalWaitMs);
                throw ex;
            }
            totalWaitMs += waitMs;
            logger.warn("Everfi rate limited {}. Retry {}/{} in {} ms ({} ms total backoff).",
                    requestTarget(req), retry, MAX_RETRIES, waitMs, totalWaitMs);
            try {
                Thread.sleep(waitMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Interrupted while waiting to retry Everfi rate-limited request.", e);
                throw new IOException("Interrupted while retrying rate-limited Everfi request.", e);
            }

            ResponseResult result = executeOnce(req);
            if (result.isSuccess()) {
                return result.body();
            }
            if (result.statusCode() == EXPIRED_TOKEN_CODE) {
                tokenProvider.invalidate();
                result = executeOnce(req);
                if (result.isSuccess()) {
                    return result.body();
                }
            }
            if (result.statusCode() != RATE_LIMIT_EXCEEDED) {
                throw failure(result);
            }
        }
        throw failure(new ResponseResult(RATE_LIMIT_EXCEEDED, "Exceeded retry limit for Everfi request."));
    }

    long rateLimitWaitMillis(int retryCount, long elapsedWaitMs) throws EverfiApiException {
        if (retryCount < 1 || elapsedWaitMs < 0) {
            throw new IllegalArgumentException("Retry count must be positive and elapsed wait must not be negative.");
        }
        long nextWaitMs = exponentialBackoffMillis(retryCount);
        if (elapsedWaitMs > maxRateLimitWaitMs - nextWaitMs) {
            throw failure(new ResponseResult(
                    RATE_LIMIT_EXCEEDED, "Exceeded maximum rate-limit wait of " + maxRateLimitWaitMs + " ms."));
        }
        return nextWaitMs;
    }

    private String requestTarget(HttpUriRequest req) {
        return req.getMethod() + " " + req.getURI().getPath();
    }

    private record ResponseResult(int statusCode, String body) {
        boolean isSuccess() {
            return isSuccessStatus(statusCode);
        }
    }

    static boolean isSuccessStatus(int statusCode) {
        return statusCode >= SUCCESS_STATUS_MIN && statusCode <= SUCCESS_STATUS_MAX;
    }

    private EverfiApiException failure(ResponseResult result) {
        logger.info(String.format("Received unknown response from Everfi: '%s %s'",
                result.statusCode(), result.body()));
        return new EverfiApiException(result.statusCode(), result.body());
    }

    /**
     * Returns the next wait interval, in milliseconds, using an exponential
     * backoff algorithm.
     * First retry waits 400ms, next 800ms, then 1,600ms, etc...
     */
    private long exponentialBackoffMillis(int retryCount) {
        return 200L << retryCount;
    }

    private void updateHeaders(HttpUriRequest req) throws IOException {
        req.removeHeaders("Accept");
        req.removeHeaders("Content-Type");
        req.removeHeaders("Authorization");

        req.addHeader("Accept", "application/json");
        req.addHeader("Content-Type", "application/json");
        req.addHeader("Authorization", "Bearer " + tokenProvider.getAccessToken());
    }
}
