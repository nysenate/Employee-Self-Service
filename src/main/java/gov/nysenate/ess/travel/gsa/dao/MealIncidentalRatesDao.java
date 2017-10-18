package gov.nysenate.ess.travel.gsa.dao;

import gov.nysenate.ess.travel.application.model.MealIncidentalRate;

public interface MealIncidentalRatesDao {

    void insertMealIncidentalRates(MealIncidentalRate[] mealIncidentalRates);

    void updateMealIncidentalRates(MealIncidentalRate[] mealIncidentalRates);
}
