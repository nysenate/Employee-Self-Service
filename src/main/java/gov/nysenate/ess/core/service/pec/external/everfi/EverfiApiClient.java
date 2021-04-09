package gov.nysenate.ess.core.service.pec.external.everfi;

import gov.nysenate.ess.core.util.OutputUtils;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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

    private static final String HOST = "https://api.fifoundry.net";
    private static String accessToken = "";
    private static final int SUCCESS = 200;
    private static final int CREATED = 201;
    private static final int EXPIRED_TOKEN_CODE = 401;
    private static final int RATE_LIMIT_EXCEEDED = 429;
    private static final int MAX_RETRIES = 10;
    private String clientId;
    private String clientSecret;

    public EverfiApiClient(@Value("${pec.everfi.client.id:}") String clientId,
                           @Value("${pec.everfi.client.secret:}") String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    /**
     * Makes a get reqeust to the given Everfi API endpoint.
     *
     * @param endpoint The endpoint of the API to call. Including any query parameters necessary.
     * @return The body of the response as a String, or null if an error occurred.
     * @throws IOException If there is an error making the request.
     */
    public String get(String endpoint) throws IOException {
        String url = HOST + endpoint;
        HttpUriRequest req = new HttpGet(url);
        return makeRequest(req, null);
    }

    /**
     * Makes a post request to the given Everifi API endpoint with the given body entity.
     *
     * @param endpoint The endpoint of the API to call. Including any query parameters necessary.
     * @param body     The body of the post request to be sent.
     * @return The body of the response as a String, or null if an error occurred.
     * @throws IOException If there is an error making the request.
     */
    public String post(String endpoint, String body) throws IOException {
        String url = HOST + endpoint;

        HttpUriRequest post = new HttpPost(url);
        return makeRequest(post, body);
    }

    /**
     * Makes a patch request to the given Everifi API endpoint with the given body entity.
     *
     * @param endpoint The endpoint of the API to call. Including any query parameters necessary.
     * @param body     The body of the post request to be sent.
     * @return The body of the response as a String, or null if an error occurred.
     * @throws IOException If there is an error making the request.
     */
    public String patch(String endpoint, String body) throws IOException {
        String url = HOST + endpoint;

        HttpUriRequest patch = new HttpPatch(url);
        return makeRequest(patch, body);
    }

    private String makeRequest(HttpUriRequest req, String entity) throws IOException {
        if (req instanceof HttpPost) {
            ((HttpPost) req).setEntity(new StringEntity(entity));
        }
        else if (req instanceof  HttpPatch) {
            ((HttpPatch) req).setEntity(new StringEntity(entity));
        }

        boolean retry = true;
        int retryCount = 0;
        String data = null;

        do {
            updateHeaders(req); // Update headers so the auth token is updated if authentication was needed.

            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(req)) {

                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode == SUCCESS || statusCode == CREATED) {
                    retry = false;
                    data = EntityUtils.toString(response.getEntity());
                } else if (statusCode == EXPIRED_TOKEN_CODE) {
                    if (!authenticate()) {
                        logger.error("Unable to make get request to Everfi. Error authenticating.");
                        retry = false;
                    }
                } else if (statusCode == RATE_LIMIT_EXCEEDED){
                    // Increment the retry count and sleep.
                    retryCount++;
                    Thread.sleep(getWaitTimeExp(retryCount));
                    retry = true;
                } else {
                    logger.info(String.format("Received unknown response from Everfi: '%s %s'",
                            statusCode, EntityUtils.toString(response.getEntity())));
                    retry = false;
                }
            } catch (InterruptedException e) {
                retry = false;
                logger.error("Error sleeping thread after Everfi client exceeded the rate limit.");
            }
        } while (retry && (retryCount < MAX_RETRIES));

        return data;
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

    private boolean authenticate() throws IOException {
        String authEndpoint = "https://api.fifoundry.net/oauth/token?grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;

        HttpPost req = new HttpPost(authEndpoint);
        updateHeaders(req);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(req)) {

            int statusCode = response.getStatusLine().getStatusCode();
            String body = EntityUtils.toString(response.getEntity());

            logger.info("AUTHENTICATING statusCode: " + statusCode);

            if (statusCode == SUCCESS || statusCode == CREATED) {
                // Use the new access token for future requests
                EverfiAuthResponse authRes = OutputUtils.jsonToObject(body, EverfiAuthResponse.class);
                accessToken = authRes.access_token;
                return true;
            } else if (statusCode == EXPIRED_TOKEN_CODE) {
                logger.error("Error authenticating with Everfi. Check your client id and client secret values.");
                return false;
            } else {
                logger.error("Error authenticating with Everfi.");
                return false;
            }
        }
    }

    private void updateHeaders(HttpUriRequest req) {
        req.removeHeaders("Accept");
        req.removeHeaders("Content-Type");
        req.removeHeaders("Authorization");

        req.addHeader("Accept", "application/json");
        req.addHeader("Content-Type", "application/json");
        req.addHeader("Authorization", "Bearer " + accessToken);
    }
}
