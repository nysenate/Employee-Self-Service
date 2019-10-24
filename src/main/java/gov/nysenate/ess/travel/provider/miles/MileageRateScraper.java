package gov.nysenate.ess.travel.provider.miles;

import gov.nysenate.ess.core.util.HttpUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MileageRateScraper {

    private static final Logger logger = LoggerFactory.getLogger(MileageRateScraper.class);

    private HttpUtils httpUtils;
    private MileageRateParser mileageRateParser;

    @Autowired
    public MileageRateScraper(HttpUtils httpUtils, MileageRateParser mileageRateParser) {
        this.httpUtils = httpUtils;
        this.mileageRateParser = mileageRateParser;
    }

    /**
     * Scrapes a {@link MileageRate} from a gsa web page which lists current irs travel rates.
     * @return
     * @throws IOException
     */
    public MileageRate scrapeMileRates() throws IOException {
        String url = "https://www.gsa.gov/travel/plan-book/transportation-airfare-rates-pov-rates-etc/privately-owned-vehicle-pov-mileage-reimbursement-rates";
        String html = httpUtils.urlToString(url);
        return mileageRateParser.parseMileageRate(html);
    }
}