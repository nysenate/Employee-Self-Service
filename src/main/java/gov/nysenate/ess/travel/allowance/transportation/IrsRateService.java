package gov.nysenate.ess.travel.allowance.transportation;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class IrsRateService {

    @Autowired
    IrsRateDao irsRateDao;

    public double webScrapeIrsRate() throws IOException{
        Document doc = Jsoup.connect("https://www.irs.gov/tax-professionals/standard-mileage-rates").get();
        String rate = doc.select("article table tbody tr td").get(1).text();
        return Double.parseDouble(rate);
    }

    /**
     * Needs to run once a day to make sure the IRS travel rate is kept current
     */
    public void scrapeAndUpdate() {
        double webVal = -1;
        try {
            webVal = webScrapeIrsRate();
        } catch (IOException e) {
            //do nothing
        }
        double dbVal = irsRateDao.getIrsRate();
        if (webVal != dbVal) {
            irsRateDao.updateIrsRate(webVal);
        }
    }
}
