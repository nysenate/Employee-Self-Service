package gov.nysenate.ess.core.service.pec.external.everfi;

import gov.nysenate.ess.core.util.OutputUtils;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;


/**
 * This class is responsible for making API requests to the Everfi API.
 *
 * It will automatically re-authenticate with Everfi as necessary.
 */
@Service
public class EverfiApiClient {

    private static final Logger logger = LoggerFactory.getLogger(EverfiApiClient.class);

    private static final String HOST = "https://api.fifoundry.net";
    private static String accessToken = "";
    private static final int EXPIRED_TOKEN_CODE = 401;
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
        System.out.println(url);

        // TODO url encode
        HttpGet req = new HttpGet(url);
        addHeaders(req);
        req.addHeader("Authorization", "Bearer " + accessToken);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(req)) {

            int statusCode = response.getStatusLine().getStatusCode();
            String data = EntityUtils.toString(response.getEntity());

            if (statusCode == EXPIRED_TOKEN_CODE) {
                if (authenticate()) {
                    return get(endpoint);
                } else {
                    logger.error("Unable to make get request to Everfi. Error authenticating.");
                    return null;
                }
            } else {
                return data;
            }
        }
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

        HttpPost post = new HttpPost(url);
        post.setEntity(new StringEntity(body));
        addHeaders(post);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {

            int statusCode = response.getStatusLine().getStatusCode();
            String data = EntityUtils.toString(response.getEntity());

            if (statusCode == EXPIRED_TOKEN_CODE) {
                if (authenticate()) {
                    return post(endpoint, body);
                } else {
                    logger.error("Unable to make post request to Everfi. Error authenticating.");
                    return null;
                }
            } else {
                return data;
            }
        }
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

        HttpPatch patch = new HttpPatch(url);
        patch.setEntity(new StringEntity(body));
        addHeaders(patch);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(patch)) {

            int statusCode = response.getStatusLine().getStatusCode();
            String data = EntityUtils.toString(response.getEntity());

            if (statusCode == EXPIRED_TOKEN_CODE) {
                if (authenticate()) {
                    return patch(endpoint, body);
                } else {
                    logger.error("Unable to make patch request to Everfi. Error authenticating.");
                    return null;
                }
            } else {
                return data;
            }
        }
    }

    private boolean authenticate() throws IOException {
        String authEndpoint = "https://api.fifoundry.net/oauth/token?grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;

        HttpPost req = new HttpPost(authEndpoint);
        addHeaders(req);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(req)) {

            int statusCode = response.getStatusLine().getStatusCode();
            String body = EntityUtils.toString(response.getEntity());

            if (statusCode == 200) {
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

    private void addHeaders(AbstractHttpMessage req) {
        req.addHeader("Accept", "application/json");
        req.addHeader("Content-Type", "application/json");
    }
}
