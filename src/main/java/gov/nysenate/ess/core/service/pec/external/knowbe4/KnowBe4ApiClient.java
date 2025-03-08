package gov.nysenate.ess.core.service.pec.external.knowbe4;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class KnowBe4ApiClient {

    private static final Logger logger = LoggerFactory.getLogger(KnowBe4ApiClient.class);
    private static final int SUCCESS = 200;
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int INCORRECT_RESPONSE_FORMAT = 406;
    private static final int TOO_MANY_REQUESTS = 429;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int MAX_RETRIES = 10;
    private final String HOST;
    private final String KnowBe4ApiKey;

    public KnowBe4ApiClient(@Value("${pec.KnowBe4Host}") String host,
                            @Value("${pec.KnowBe4ApiKey:}") String KnowBe4ApiKey) {
        this.HOST = host;
        this.KnowBe4ApiKey = KnowBe4ApiKey;
    }

    /**
     * Makes a get reqeust to the given KB4 API endpoint.
     *
     * @param endpoint The endpoint of the API to call. Including any query parameters necessary.
     * @return The body of the response as a String, or null if an error occurred.
     * @throws IOException If there is an error making the request.
     */
    public String get(String endpoint) throws IOException {
        String url = HOST + endpoint;
        HttpUriRequest req = new HttpGet(url);
        return makeRequest(req);
    }

    private String makeRequest(HttpUriRequest req) throws IOException {

        updateHeaders(req); //Authentication is included here

        boolean retry = true;
        int retryCount = 0;
        String data = null;

        do {
            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(req)) {

                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode == SUCCESS) {
                    retry = false;
                    data = EntityUtils.toString(response.getEntity());
                } else if (statusCode == TOO_MANY_REQUESTS) {
                    // Increment the retry count and sleep.
                    retryCount++;
                    Thread.sleep(getWaitTimeExp(retryCount));
                } else if (statusCode == UNAUTHORIZED) {
                    logger.error("Received UNAUTHORIZED response from KnowBe4: '{} {}'",
                            statusCode, EntityUtils.toString(response.getEntity()));
                    retry = false;
                } else if (statusCode == FORBIDDEN) {
                    logger.error("Received FORBIDDEN response from KnowBe4: '{} {}'",
                            statusCode, EntityUtils.toString(response.getEntity()));
                    retry = false;
                } else if (statusCode == NOT_FOUND) {
                    logger.error("Received NOT_FOUND response from KnowBe4: '{} {}'",
                            statusCode, EntityUtils.toString(response.getEntity()));
                    retry = false;
                } else if (statusCode == INCORRECT_RESPONSE_FORMAT) {
                    logger.error("Received INCORRECT_RESPONSE_FORMAT response from KnowBe4: '{} {}'",
                            statusCode, EntityUtils.toString(response.getEntity()));
                    retry = false;
                } else if (statusCode == INTERNAL_SERVER_ERROR) {
                    logger.error("Received INTERNAL_SERVER_ERROR response from KnowBe4: '{} {}'",
                            statusCode, EntityUtils.toString(response.getEntity()));
                    retry = false;
                } else if (statusCode == SERVICE_UNAVAILABLE) {
                    logger.error("Received SERVICE_UNAVAILABLE response from KnowBe4: '{} {}'",
                            statusCode, EntityUtils.toString(response.getEntity()));
                    retry = false;
                } else {
                    logger.error(String.format("Received unknown response from KnowBe4: '%s %s'",
                            statusCode, EntityUtils.toString(response.getEntity())));
                    retry = false;
                }
            } catch (InterruptedException e) {
                retry = false;
                logger.error("Error sleeping thread after KnowBe4 client exceeded the rate limit.");
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

    private void updateHeaders(HttpUriRequest req) {
        req.removeHeaders("Accept");
        req.removeHeaders("Content-Type");
        req.removeHeaders("Authorization");

        req.addHeader("Accept", "application/json");
        req.addHeader("Content-Type", "application/json");
        req.addHeader("Authorization", "Bearer " + KnowBe4ApiKey);
    }

}
