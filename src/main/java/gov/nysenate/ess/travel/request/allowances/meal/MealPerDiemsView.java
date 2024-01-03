package gov.nysenate.ess.travel.request.allowances.meal;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.utils.Dollars;

import java.util.List;
import java.util.stream.Collectors;

public class MealPerDiemsView implements ViewObject {

    private List<MealPerDiemView> allMealPerDiems;
    private List<MealPerDiemView> requestedMealPerDiems;
    private String totalPerDiem;
    @JsonProperty("isOverridden")
    private boolean isOverridden;
    private double overrideRate;
    @JsonProperty("isAllowedMeals")
    private boolean isAllowedMeals;

    public MealPerDiemsView() {
    }

    public MealPerDiemsView(MealPerDiems ma) {
        this.allMealPerDiems = ma.allMealPerDiems().stream()
                .map(MealPerDiemView::new)
                .collect(Collectors.toList());
        this.requestedMealPerDiems = ma.requestedMealPerDiems().stream()
                .map(MealPerDiemView::new)
                .collect(Collectors.toList());
        this.totalPerDiem = ma.total().toString();
        this.isOverridden = ma.isOverridden();
        this.overrideRate = Double.parseDouble(ma.overrideRate().toString());
        this.isAllowedMeals = ma.isAllowedMeals();
    }

    public MealPerDiems toMealPerDiems() {
        return new MealPerDiems(
                allMealPerDiems.stream().map(MealPerDiemView::toMealPerDiem).collect(Collectors.toList()),
                new Dollars(overrideRate),
                isAllowedMeals);
    }

    public List<MealPerDiemView> getAllMealPerDiems() {
        return allMealPerDiems;
    }

    public List<MealPerDiemView> getRequestedMealPerDiems() {
        return requestedMealPerDiems;
    }

    public String getTotalPerDiem() {
        return totalPerDiem;
    }

    @JsonProperty("isOverridden")
    public boolean isOverridden() {
        return isOverridden;
    }

    public double getOverrideRate() {
        return overrideRate;
    }

    @JsonProperty("isAllowedMeals")
    public boolean isAllowedMeals() {
        return isAllowedMeals;
    }

    @Override
    public String getViewType() {
        return "meal-allowances";
    }
}
