package gov.nysenate.ess.travel.miles;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MileageRateScraper {

    private static final Logger logger = LoggerFactory.getLogger(MileageRateScraper.class);

    public MileageRate scrapeMileRates() throws IOException {
        String content;
        String url = "https://www.gsa.gov/travel/plan-book/transportation-airfare-rates-pov-rates-etc/privately-owned-vehicle-pov-mileage-reimbursement-rates";
        HttpGet httpget = new HttpGet(url);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpget)) {

            if (response.getStatusLine().getStatusCode() == 200) {
                content = EntityUtils.toString(response.getEntity());
                MileageRateParser mileageRateParser = new MileageRateParser();
                MileageRate parsedRate = mileageRateParser.parseMileageRate(content);
                return parsedRate;
            }
            else {
                logger.warn("Could not parse IRS Mileage rate! Status code " + response.getStatusLine().getStatusCode());
                return null;
            }
        }
    }
}