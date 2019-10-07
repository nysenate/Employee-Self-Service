package gov.nysenate.ess.travel.application.allowances.meal;

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
    private String overrideRate;

    public MealPerDiemsView() {
    }

    public MealPerDiemsView(MealPerDiems ma) {
        this.allMealPerDiems = ma.allMealPerDiems().stream()
                .map(MealPerDiemView::new)
                .collect(Collectors.toList());
        this.requestedMealPerDiems = ma.requestedMealPerDiems().stream()
                .map(MealPerDiemView::new)
                .collect(Collectors.toList());
        this.totalPerDiem = ma.totalPerDiem().toString();
        this.isOverridden = ma.isOverridden();
        this.overrideRate = ma.overrideRate().toString();
    }

    public MealPerDiems toMealPerDiems() {
        return new MealPerDiems(allMealPerDiems.stream()
                .map(MealPerDiemView::toMealPerDiem)
                .collect(Collectors.toList()), new Dollars(overrideRate));
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

    public boolean isOverridden() {
        return isOverridden;
    }

    public String getOverrideRate() {
        return overrideRate;
    }

    @Override
    public String getViewType() {
        return "meal-allowances";
    }
}
