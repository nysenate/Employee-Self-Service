package gov.nysenate.ess.core.service.pec.external.everfi;

import gov.nysenate.ess.core.util.OutputUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class EverfiApiClient {

    private static final String HOST = "https://api.fifoundry.net";
    private static String accessToken = "";
    private static final int EXPIRED_TOKEN_CODE = 401;
    private String clientId;
    private String clientSecret;

    public EverfiApiClient(@Value("${pec.everfi.client.id}") String clientId,
                           @Value("${pec.everfi.client.secret}") String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

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
            String body = EntityUtils.toString(response.getEntity());

            if (statusCode == EXPIRED_TOKEN_CODE) {
                if (authenticate()) {
                    return get(endpoint);
                }
                else {
                    // TODO ERROR refreshing access token.
                }
            }
            else {
                return body;
            }
        }

        return "";
    }

    // TODO set protected
    // TODO Return stuff
    public boolean authenticate() throws IOException {
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
            }
            else if (statusCode == EXPIRED_TOKEN_CODE) {
                // TODO ERROR authenticating... client id or client secret may be incorrect.
                return false;
            }
            else {
                // TODO Unknown error authenticating.
                return false;
            }

        }
    }

    private void addHeaders(AbstractHttpMessage req) {
        req.addHeader("Accept", "application/json");
        req.addHeader("Content-Type", "application/json");
    }
}
