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

    private final String HOST;
    private final CloseableHttpClient everfiHttpClient;
    private final EverfiClientTokenProvider tokenProvider;

    private static final int SUCCESS = 200;
    private static final int CREATED = 201;
    private static final int NOT_FOUND = 404;
    private static final int EXPIRED_TOKEN_CODE = 401;
    private static final int RATE_LIMIT_EXCEEDED = 429;
    private static final int MAX_RETRIES = 10;

    public EverfiApiClient(@Value("${pec.everfi.host}") String host,
                           CloseableHttpClient everfiHttpClient,
                           EverfiClientTokenProvider tokenProvider) {
        this.HOST = host;
        this.everfiHttpClient = everfiHttpClient;
        this.tokenProvider = tokenProvider;
    }

    /**
     * Makes a get reqeust to the given Everfi API endpoint.
     *
     * @param endpoint The endpoint of the API to call. Including any query parameters necessary.
     * @return The body of the response as a String.
     * @throws IOException If there is an error making the request.
     */
    public String get(String endpoint) throws IOException {
        return makeRequest(new HttpGet(HOST + endpoint), null);
    }

    /**
     * Makes a post request to the given Everifi API endpoint with the given body entity.
     *
     * @param endpoint The endpoint of the API to call. Including any query parameters necessary.
     * @param body     The body of the post request to be sent.
     * @return The body of the response as a String.
     * @throws IOException If there is an error making the request.
     */
    public String post(String endpoint, String body) throws IOException {
        return makeRequest(new HttpPost(HOST + endpoint), body);
    }

    /**
     * Makes a patch request to the given Everifi API endpoint with the given body entity.
     *
     * @param endpoint The endpoint of the API to call. Including any query parameters necessary.
     * @param body     The body of the post request to be sent.
     * @return The body of the response as a String.
     * @throws IOException If there is an error making the request.
     */
    public String patch(String endpoint, String body) throws IOException {
        return makeRequest(new HttpPatch(HOST + endpoint), body);
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
        updateHeaders(req);
        try (CloseableHttpResponse response = everfiHttpClient.execute(req)) {
            int status = response.getStatusLine().getStatusCode();
            String body = response.getEntity() == null ? null : EntityUtils.toString(response.getEntity());
            return new ResponseResult(status, body);
        }
    }

    private String retryRateLimited(HttpUriRequest req) throws IOException {
        for (int retry = 1; retry <= MAX_RETRIES; retry++) {
            try {
                Thread.sleep(getWaitTimeExp(retry));
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

    private record ResponseResult(int statusCode, String body) {
        boolean isSuccess() {
            return statusCode == SUCCESS || statusCode == CREATED;
        }
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
    private long getWaitTimeExp(int retryCount) {
        if (0 == retryCount) {
            return 0;
        }
        return ((long) Math.pow(2, retryCount) * 200L);
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
