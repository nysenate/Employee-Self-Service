package gov.nysenate.ess.travel.allowance.gsa.dao;

import gov.nysenate.ess.travel.allowance.gsa.model.MealIncidentalRate;

public interface MealIncidentalRatesDao {

    void insertMealIncidentalRates(MealIncidentalRate[] mealIncidentalRates);

    void updateMealIncidentalRates(MealIncidentalRate[] mealIncidentalRates);
}
