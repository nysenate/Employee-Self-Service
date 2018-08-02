package gov.nysenate.ess.travel.application.allowances.meal;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.List;
import java.util.stream.Collectors;

public class MealAllowancesView implements ViewObject {

    List<MealAllowanceView> mealAllowances;
    String totalMealAllowance;

    public MealAllowancesView() {
    }

    public MealAllowancesView(MealAllowances allowances) {
        this.mealAllowances = allowances.getMealAllowances().stream()
                .map(MealAllowanceView::new)
                .collect(Collectors.toList());
        this.totalMealAllowance = allowances.totalAllowance().toString();
    }

    public MealAllowances toMealAllowances() {
        return new MealAllowances(mealAllowances.stream()
                .map(MealAllowanceView::toMealAllowance)
                .collect(Collectors.toList()));
    }

    public List<MealAllowanceView> getMealAllowances() {
        return mealAllowances;
    }

    public String getTotalMealAllowance() {
        return totalMealAllowance;
    }

    @Override
    public String getViewType() {
        return "meal-allowances";
    }
}
