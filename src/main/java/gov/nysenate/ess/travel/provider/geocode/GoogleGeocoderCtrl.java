package gov.nysenate.ess.travel.provider.geocode;

import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * A simple wrapper around the google geocoding API.
 */
@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/geocode")
public class GoogleGeocoderCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(GoogleGeocoderCtrl.class);
    private static final String URL_FORMAT = "https://maps.googleapis.com/maps/api/geocode/json?key=%s&address=%s";

    private final String key;

    @Autowired
    public GoogleGeocoderCtrl(@Value("${google.maps.api.key:}") String key) {
        this.key = key;
    }

    /**
     * Geocodes an address, returning the response from google's api.
     * <p>
     *     (GET) /api/v1/travel/geocode
     * <p>
     *     Request Param: address (string) - The address to geocode. e.g 100 State Street Albany NY 12207
     * @param address
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<byte[]> geocodeAddress(@RequestParam String address) throws IOException {
        String addr = URLEncoder.encode(address, "UTF-8");
        String url = String.format(URL_FORMAT, key, addr);

        HttpGet httpget = new HttpGet(url);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpget)) {
            HttpHeaders headers = new HttpHeaders();
            for (Header header : response.getAllHeaders()) {
                headers.set(header.getName(), header.getValue());
            }
            return new ResponseEntity<>(IOUtils.toByteArray(response.getEntity().getContent()),
                    headers, HttpStatus.valueOf(response.getStatusLine().getStatusCode()));
        }
    }
}
