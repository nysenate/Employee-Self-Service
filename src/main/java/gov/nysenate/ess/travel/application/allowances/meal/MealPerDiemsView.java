package gov.nysenate.ess.travel.application.allowances.meal;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.utils.Dollars;

import java.util.List;
import java.util.stream.Collectors;

public class MealPerDiemsView implements ViewObject {

    private int id;
    private List<MealPerDiemView> allMealPerDiems;
    private List<MealPerDiemView> requestedMealPerDiems;
    private String totalPerDiem;
    private boolean isOverridden;
    private double overrideRate;

    public MealPerDiemsView() {
    }

    public MealPerDiemsView(MealPerDiems ma) {
        this.id = ma.id();
        this.allMealPerDiems = ma.allMealPerDiems().stream()
                .map(MealPerDiemView::new)
                .collect(Collectors.toList());
        this.requestedMealPerDiems = ma.requestedMealPerDiems().stream()
                .map(MealPerDiemView::new)
                .collect(Collectors.toList());
        this.totalPerDiem = ma.totalPerDiem().toString();
        this.isOverridden = ma.isOverridden();
        this.overrideRate = Double.parseDouble(ma.overrideRate().toString());
    }

    public MealPerDiems toMealPerDiems() {
        return new MealPerDiems(id, allMealPerDiems.stream()
                .map(MealPerDiemView::toMealPerDiem)
                .collect(Collectors.toList()), new Dollars(overrideRate));
    }

    public int getId() {
        return id;
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

    @Override
    public String getViewType() {
        return "meal-allowances";
    }
}
