package gov.nysenate.ess.travel.application.dao;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Repository
public class IrsRateDao {
    private double irsRate;  // business travel rate in cents per mile

    private IrsRateDao() {
        webScrapeIrsRate();
    }

    public void webScrapeIrsRate() {
        try {
            Document doc = Jsoup.connect("https://www.irs.gov/tax-professionals/standard-mileage-rates").get();
            String rate = doc.select("article table tbody tr td").get(1).text();
            irsRate = Double.parseDouble(rate);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double getIrsRate() {
        return irsRate;
    }
}
