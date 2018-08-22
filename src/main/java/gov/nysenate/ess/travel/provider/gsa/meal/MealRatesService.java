package gov.nysenate.ess.travel.provider.gsa.meal;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class MealRatesService {

    private MealRatesParser mealRatesParser;
    private SqlMealRatesDao sqlMealRatesDao;
    private static final Logger logger = LoggerFactory.getLogger(MealRatesService.class);

    @Autowired
    public MealRatesService(SqlMealRatesDao sqlMealRatesDao, MealRatesParser mealRatesParser) {
        this.sqlMealRatesDao = sqlMealRatesDao;
        this.mealRatesParser = mealRatesParser;
    }

    @Scheduled(cron = "${cache.cron.meal.rate}")
    public void scrapeAndUpdateCron() throws IOException {
        scrapeAndUpdate();
    }

    public MealRatesView scrapeAndUpdate() throws IOException {
        MealRates scrapedValues = this.scrapeMealRates();
        MealRates dbValues = sqlMealRatesDao.getMealRates(LocalDate.now());

        if (scrapedValues != null && !scrapedValues.equals(dbValues)) {
            sqlMealRatesDao.insertMealRates(scrapedValues, LocalDate.now());
            return new MealRatesView(scrapedValues);
        }
        else {
            return new MealRatesView(dbValues);
        }
    }

    private MealRates scrapeMealRates() throws IOException {
        String content;
        String url = "https://www.gsa.gov/travel/plan-book/per-diem-rates/fy2018-mie-breakdown";
        HttpGet httpget = new HttpGet(url);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpget)) {

            content = EntityUtils.toString(response.getEntity());
            if (response.getStatusLine().getStatusCode() == 200) {
                return mealRatesParser.parseMealRates(content);
            }
            else {
                logger.warn("Unable To Scrape meal tiers at " + LocalDateTime.now());
                return null;
            }
        }

    }
}