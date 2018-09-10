package gov.nysenate.ess.travel.provider.gsa.meal;

import com.google.common.collect.Sets;
import gov.nysenate.ess.core.util.DateUtils;
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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class MealRatesService {

    private static final Logger logger = LoggerFactory.getLogger(MealRatesService.class);

    private MealTiersParser mealTiersParser;
    private SqlMealRatesDao mealRatesDao;

    @Autowired
    public MealRatesService(SqlMealRatesDao mealRatesDao, MealTiersParser mealTiersParser) {
        this.mealRatesDao = mealRatesDao;
        this.mealTiersParser = mealTiersParser;
    }

    @Scheduled(cron = "${cache.cron.meal.rate}")
    public void scrapeAndUpdateCron() throws IOException {
        logger.info("Scraping GSA meal tiers...");
        scrapeAndUpdate();
    }

    public MealRates scrapeAndUpdate() throws IOException {
        Set<MealTier> scrapedTiers = this.scrapeMealTiers();
        MealRates currentMealRates = mealRatesDao.getMealRates(LocalDate.now());
        logger.info("Scraped meal tiers: " + scrapedTiers.toString());

        Set<MealTier> currentTiers = Sets.newHashSet(currentMealRates.getTiers());
        if (!scrapedTiers.equals(currentTiers)) {
            logger.info("Scraped meal tiers were new, updating old meal rates and inserting.");
            // Update old rate end date
            currentMealRates.setEndDate(LocalDate.now().minusDays(1));
            mealRatesDao.updateMealRates(currentMealRates);

            // Insert new rate which is effective starting today.
            MealRates newRates = new MealRates(UUID.randomUUID(), LocalDate.now(), DateUtils.THE_FUTURE, scrapedTiers);
            mealRatesDao.insertMealRates(newRates);
            return newRates;
        }
        else {
            return currentMealRates;
        }
    }

    private Set<MealTier> scrapeMealTiers() throws IOException {
        String content;
        String url = "https://www.gsa.gov/mie";
        HttpGet httpget = new HttpGet(url);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpget)) {

            content = EntityUtils.toString(response.getEntity());
            if (response.getStatusLine().getStatusCode() == 200) {
                return mealTiersParser.parseMealTiers(content);
            }
            else {
                logger.warn("Unable To Scrape meal tiers at " + LocalDateTime.now() + " Response was: " + content);
                return new HashSet<>();
            }
        }

    }
}