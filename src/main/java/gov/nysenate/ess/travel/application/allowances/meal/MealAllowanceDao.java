package gov.nysenate.ess.travel.application.allowances.meal;

import java.util.UUID;

public interface MealAllowanceDao {

    void insertMealAllowances(UUID versionId, MealAllowances mealAllowances);

    MealAllowances getMealAllowances(UUID versionId);
}
