package gov.nysenate.ess.travel.allowance.gsa.service;

import gov.nysenate.ess.travel.allowance.gsa.dao.MealIncidentalRatesDao;
import gov.nysenate.ess.travel.allowance.gsa.model.MealIncidentalRate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MealIncidentalRatesService {

    private MealIncidentalRatesDao mealIncidentalRatesDao;

    @Autowired
    public MealIncidentalRatesService(MealIncidentalRatesDao mealIncidentalRatesDao) {
        this.mealIncidentalRatesDao = mealIncidentalRatesDao;
    }

    public void insertMealIncidentalRates() {
        mealIncidentalRatesDao.insertMealIncidentalRates(getMealIncidentalRates());
    }

    public void updateMealIncidentalRates() {
        mealIncidentalRatesDao.updateMealIncidentalRates(getMealIncidentalRates());
    }

    private MealIncidentalRate[] getMealIncidentalRates() {
        // TODO Checked scraped content with values in database
        Document document = null;
        try {
            document = Jsoup.connect("https://www.gsa.gov/travel/plan-book/per-diem-rates/meals-and-incidental-expenses-mie-breakdown").get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements elements = document.select("table tbody tr td");
        MealIncidentalRate[] mealIncidentalRates = new MealIncidentalRate[elements.size() / 6];

        for (int i = 0, j = 0; i < elements.size(); i += 6, j++) {
            // Substring gets rid of $
            int total = Integer.parseInt(elements.get(i).text().substring(1));
            int breakfast = Integer.parseInt(elements.get(i + 1).text().substring(1));
            int dinner = Integer.parseInt(elements.get(i + 3).text().substring(1));
            int incidental = Integer.parseInt(elements.get(i + 4).text().substring(1));

            mealIncidentalRates[j] = new MealIncidentalRate(total, breakfast, dinner, incidental);
        }

        return mealIncidentalRates;
    }
}
