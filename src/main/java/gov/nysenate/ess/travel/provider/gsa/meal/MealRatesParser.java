package gov.nysenate.ess.travel.provider.gsa.meal;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.HashSet;


@Component
public class MealRatesParser {

    public MealRates parseMealRates(String content) {
        HashSet<MealTier> mealTiers = new HashSet<>();

        Document document = Jsoup.parse(content);
        Element tbody = document.select("table tbody").get(0);
        Elements rowEls = tbody.select("tbody>tr");
        for (Element rowEl : rowEls) {
            Elements columnEls = rowEl.getElementsByTag("td");

            if (columnEls.size() > 0) { // if not <th> columns
                String tier = columnEls.get(0).text().substring(1);
                String breakfast = columnEls.get(1).text().substring(1);
                String lunch = columnEls.get(2).text().substring(1);
                String dinner = columnEls.get(3).text().substring(1);
                String incidental = columnEls.get(4).text().substring(1);

                MealTier mealTier = new MealTier(tier, breakfast, lunch, dinner, incidental);
                mealTiers.add(mealTier);
            }
        }


        return new MealRates(mealTiers);
    }
}