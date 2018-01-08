package gov.nysenate.ess.travel.allowance.gsa.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.allowance.gsa.model.MealAllowance;

import java.util.Set;
import java.util.stream.Collectors;

public class MealAllowanceView implements ViewObject {

    private String total;
    private Set<MealDayView> mealDays;

    public MealAllowanceView() {
    }

    public MealAllowanceView(MealAllowance mealAllowance) {
        this.total = mealAllowance.getTotal().toString();
        mealDays = mealAllowance.getMealDays().stream()
                .map(MealDayView::new)
                .collect(Collectors.toSet());
    }

    public MealAllowance toMealAllowance() {
        return new MealAllowance(getMealDays().stream()
                .map(MealDayView::toMealDay)
                .collect(Collectors.toSet()));
    }

    public String getTotal() {
        return total;
    }

    public Set<MealDayView> getMealDays() {
        return mealDays;
    }

    @Override
    public String getViewType() {
        return "meal-allowance";
    }
}
