package gov.nysenate.ess.travel.allowance.gsa.service;

import gov.nysenate.ess.travel.allowance.gsa.model.MealRate;
import gov.nysenate.ess.travel.allowance.gsa.model.MealTier;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
public class MealRatesParser {

    public MealRate scrapeMealRates(String content) {
        Map<String, MealTier> mealTiers = new HashMap<>();

        Document document = Jsoup.parse(content);
        Elements rowEls = document.select("table tbody tr");
        for (Element rowEl : rowEls) {
            Elements columnEls = rowEl.getElementsByTag("td");

            if (columnEls.size() > 0) { // if not <th> columns
                String tier = columnEls.get(0).text().substring(1);
                String breakfast = columnEls.get(1).text().substring(1);
                String lunch = columnEls.get(2).text().substring(1);
                String dinner = columnEls.get(3).text().substring(1);
                String incidental = columnEls.get(4).text().substring(1);

                MealTier mealTier = new MealTier(tier, breakfast, lunch, dinner, incidental);
                mealTiers.put(mealTier.getTier(), mealTier);
            }
        }

        return new MealRate(LocalDate.now(), null, mealTiers);
    }
}
