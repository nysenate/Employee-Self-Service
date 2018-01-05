package gov.nysenate.ess.travel.allowance.gsa.dao;

import gov.nysenate.ess.travel.allowance.gsa.model.MealRates;

import java.time.LocalDate;

public interface MealRatesDao {

    /**
     * Get MealRates effective on the given date.
     * @param date
     * @return
     */
    MealRates getMealRates(LocalDate date);

    /**
     * Inserts new MealRates with an effective start date = startDate.
     * Updates the currently active MealRates setting its endDate to
     * startDate - 1day.
     * @param mealRates
     * @param startDate
     */
    void insertMealRates(MealRates mealRates, LocalDate startDate);
}
