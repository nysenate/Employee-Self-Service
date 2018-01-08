package gov.nysenate.ess.travel.allowance.gsa.service;

import gov.nysenate.ess.core.util.HttpUtils;
import gov.nysenate.ess.travel.allowance.gsa.dao.MealRatesDao;
import gov.nysenate.ess.travel.allowance.gsa.model.MealRates;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;

@Service
public class MealRatesSrapeService {

    private MealRatesParser mealRatesParser;
    private MealRatesDao mealRatesDao;

    @Autowired
    public MealRatesSrapeService(MealRatesDao mealRatesDao, MealRatesParser mealRatesParser) {
        this.mealRatesDao = mealRatesDao;
        this.mealRatesParser = mealRatesParser;
    }

    @Scheduled(cron = "${scheduler.travel.scrape.cron}")
    public void scrapeAndUpdate() throws IOException {
        MealRates scrapedRates = scrapeMealTiers();
        MealRates currentRates = mealRatesDao.getMealRates(LocalDate.now());

        if (!scrapedRates.equals(currentRates)) {
            // TODO Further verification of scraped data?
            mealRatesDao.insertMealRates(scrapedRates, LocalDate.now());
        }
    }

    private MealRates scrapeMealTiers() throws IOException {
        String url = "http://www.gsa.gov/mie";
        String content = HttpUtils.urlToString(url);
        return mealRatesParser.parseMealRates(content);
    }
}
