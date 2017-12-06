package gov.nysenate.ess.travel.allowance.mileage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
public class IrsRateService {

    @Autowired private IrsRateDao irsRateDao;
    @Value("${travel.irs.link}") private String irsLink;

    @PostConstruct
    public void postConstruct() {
        // Ensure the database has an initialized value.
        this.scrapeAndUpdate();
    }

    public double webScrapeIrsRate() throws IOException{
        Document doc = Jsoup.connect(irsLink).get();
        String rate = doc.select("article table tbody tr td").get(1).text();
        return Double.parseDouble(rate);
    }

    @Scheduled(cron = "${scheduler.travel.scrape.cron}")
    public void scrapeAndUpdate() {
        double webVal = -1;
        try {
            webVal = webScrapeIrsRate();
        } catch (IOException e) {
            //do nothing
        }
        double dbVal = irsRateDao.getIrsRate();
        if (dbVal == -2) {  //nothing in the table
            irsRateDao.insertIrsRate(webVal);
        }
        else if (webVal != dbVal) {
            irsRateDao.updateIrsRate(webVal);
        }
    }
}
