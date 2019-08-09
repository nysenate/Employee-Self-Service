package gov.nysenate.ess.travel.application.allowances.meal;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.List;
import java.util.stream.Collectors;

public class MealPerDiemsView implements ViewObject {

    private List<MealPerDiemView> allMealPerDiems;
    private List<MealPerDiemView> requestedMealPerDiems;
    private String requestedPerDiem;
    private String maximumPerDiem;

    public MealPerDiemsView() {
    }

    public MealPerDiemsView(MealPerDiems ma) {
        this.allMealPerDiems = ma.allMealPerDiems().stream()
                .map(MealPerDiemView::new)
                .collect(Collectors.toList());
        this.requestedMealPerDiems = ma.requestedMealPerDiems().stream()
                .map(MealPerDiemView::new)
                .collect(Collectors.toList());
        this.requestedPerDiem = ma.requestedPerDiem().toString();
        this.maximumPerDiem = ma.maximumPerDiem().toString();
    }

    public MealPerDiems toMealPerDiems() {
        return new MealPerDiems(allMealPerDiems.stream()
                .map(MealPerDiemView::toMealPerDiem)
                .collect(Collectors.toList()));
    }

    public List<MealPerDiemView> getAllMealPerDiems() {
        return allMealPerDiems;
    }

    public List<MealPerDiemView> getRequestedMealPerDiems() {
        return requestedMealPerDiems;
    }

    public String getRequestedPerDiem() {
        return requestedPerDiem;
    }

    public String getMaximumPerDiem() {
        return maximumPerDiem;
    }

    @Override
    public String getViewType() {
        return "meal-allowances";
    }
}
