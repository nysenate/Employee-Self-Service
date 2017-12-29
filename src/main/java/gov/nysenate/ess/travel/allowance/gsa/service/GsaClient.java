package gov.nysenate.ess.travel.allowance.gsa.service;

import gov.nysenate.ess.travel.allowance.gsa.GsaClientException;
import gov.nysenate.ess.travel.allowance.gsa.model.GsaResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.Month;

/**
 * GSA API Docs: https://www.gsa.gov/technology/government-it-initiatives/digital-strategy/per-diem-apis/api-for-per-diem-rates
 */
@Component
public class GsaClient {

    private String baseUrl;
    private GsaResponseParser gsaResponseParser;
    private static final Logger logger = LoggerFactory.getLogger(GsaClient.class);

    @Autowired
    public GsaClient(@Value("${travel.gsa.link}") String baseUrl, GsaResponseParser gsaResponseParser) {
        this.baseUrl = baseUrl;
        this.gsaResponseParser = gsaResponseParser;
    }

    public GsaResponse queryGsa(LocalDate date, String zip) throws IOException {
        return doQueryGsa(date, zip);
    }

    private GsaResponse doQueryGsa(LocalDate date, String zip) throws IOException {
        int fiscalYear = getFiscalYear(date);

        String query = "{\"FiscalYear\":" + String.valueOf(fiscalYear)
                + ",\"Zip\":" + zip + "}";
        String url = baseUrl + URLEncoder.encode(query, "UTF-8");
        HttpGet httpget = new HttpGet(url);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpget)) {

            if (response.getStatusLine().getStatusCode() != 200) {
                return handleUnsuccessfulQuery(httpget, response);
            }
            String jsonResponse = EntityUtils.toString(response.getEntity());
            return gsaResponseParser.parseGsaResponse(jsonResponse);
        }
    }

    private int getFiscalYear(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();

        int fiscalYear = year;
        if (month >= Month.OCTOBER.getValue()) {
            fiscalYear++;
        }
        return fiscalYear;
    }

    private GsaResponse handleUnsuccessfulQuery(HttpGet httpget, CloseableHttpResponse response) throws IOException {
        logger.warn("GSA API returned a status code of " + response.getStatusLine().getStatusCode() + "\n" +
                "from URL : " + httpget.getURI().toString() + "\n" +
                "with entity: " + (response.getEntity() == null ? "" : EntityUtils.toString(response.getEntity())));
        throw new GsaClientException("GSA API returned a status code of : " + response.getStatusLine().getStatusCode());
    }
}
