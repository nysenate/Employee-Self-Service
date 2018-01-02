package gov.nysenate.ess.travel.allowance.gsa.service;

import gov.nysenate.ess.travel.allowance.gsa.model.MealTier;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MealTiersParser {

    /**
     * Parses a Map of {@link MealTier}'s from the gsa
     * Meal and Incidental expenses webpage html.
     * Webpage available at: http://www.gsa.gov/mie
     *
     * @param content
     * @return
     */
    public Map<String, MealTier> parseMealTiers(String content) {
        Document document = Jsoup.parse(content);
        Elements rows = document.select("table tbody tr:has(td)");
        return rows.stream()
                .map(this::createTierFromRow)
                .collect(Collectors.toMap(MealTier::getTier, Function.identity()));
    }

    private MealTier createTierFromRow(Element row) {
        Elements columnEls = row.getElementsByTag("td");
        String tier = format(columnEls.get(0).text());
        String breakfast = format(columnEls.get(1).text());
        String lunch = format(columnEls.get(2).text());
        String dinner = format(columnEls.get(3).text());
        String incidental = format(columnEls.get(4).text());
        return new MealTier(tier, breakfast, lunch, dinner, incidental);
    }

    /** Remove the dollar sign from the values. */
    private String format(String text) {
        return text.replace("$", "");
    }
}
