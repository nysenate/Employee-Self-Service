package gov.nysenate.ess.travel.allowance.gsa.service;

import gov.nysenate.ess.travel.allowance.gsa.model.GsaResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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
//            return new GsaResponse(httpget.getURI().toString(), response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity()));

            // TODO handle errors, non 200 codes, etc...
            if (response.getStatusLine().getStatusCode() == 200) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                return gsaResponseParser.parseGsaResponse(jsonResponse);
            }
        }
        return null;
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
}
