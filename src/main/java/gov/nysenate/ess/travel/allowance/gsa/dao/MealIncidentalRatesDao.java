package gov.nysenate.ess.travel.allowance.gsa.dao;

import gov.nysenate.ess.travel.allowance.gsa.model.MealRate;

public interface MealIncidentalRatesDao {

    MealRate[] getMealIncidentalRates();

    void insertMealIncidentalRates(MealRate[] mealRates);

    void updateMealIncidentalRates(MealRate[] mealRates);
}
