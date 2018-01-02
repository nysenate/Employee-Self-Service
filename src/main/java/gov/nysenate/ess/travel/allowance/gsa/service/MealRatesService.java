package gov.nysenate.ess.travel.allowance.gsa.service;

import gov.nysenate.ess.travel.allowance.gsa.dao.MealIncidentalRatesDao;
import gov.nysenate.ess.travel.allowance.gsa.model.MealRate;
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
public class MealRatesService {

    private MealTiersParser mealTiersParser;
    private MealIncidentalRatesDao mealIncidentalRatesDao;

    @Autowired
    public MealRatesService(MealIncidentalRatesDao mealIncidentalRatesDao, MealTiersParser mealTiersParser) {
        this.mealIncidentalRatesDao = mealIncidentalRatesDao;
        this.mealTiersParser = mealTiersParser;
    }

    @Scheduled(cron = "${scheduler.travel.scrape.cron}")
    public void scrapeAndUpdate() throws IOException {
        MealRate scrapedValues = this.scrapeMealRates();
        MealRate[] dbValues = mealIncidentalRatesDao.getMealIncidentalRates();

//        if(!Arrays.equals(scrapedValues, dbValues)){
//            mealIncidentalRatesDao.updateMealIncidentalRates(scrapedValues);
//        }
    }

    private MealRate scrapeMealRates() throws IOException {
        // TODO: similar code to httpget in GsaClient. Make into utility?
        String content;
        String url = "http://www.gsa.gov/mie";
        HttpGet httpget = new HttpGet(url);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpget)) {

            if (response.getStatusLine().getStatusCode() != 200) {
                // TODO
            }
            content = EntityUtils.toString(response.getEntity());
        }

        return new MealRate(LocalDate.now(), null, mealTiersParser.parseMealTiers(content));
    }
}
