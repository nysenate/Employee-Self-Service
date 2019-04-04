package gov.nysenate.ess.travel.application.allowances.meal;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.List;
import java.util.stream.Collectors;

public class MealAllowancesView implements ViewObject {

    private List<MealPerDiemView> allMealPerDiems;
    private List<MealPerDiemView> requestedMealPerDiems;
    private String totalRequestedAllowance;

    public MealAllowancesView() {
    }

    public MealAllowancesView(MealAllowances ma) {
        this.allMealPerDiems = ma.allMealPerDiems().stream()
                .map(MealPerDiemView::new)
                .collect(Collectors.toList());
        this.requestedMealPerDiems = ma.requestedMealPerDiems().stream()
                .map(MealPerDiemView::new)
                .collect(Collectors.toList());
        this.totalRequestedAllowance = ma.totalRequestedAllowance().toString();
    }

    public List<MealPerDiemView> getAllMealPerDiems() {
        return allMealPerDiems;
    }

    public List<MealPerDiemView> getRequestedMealPerDiems() {
        return requestedMealPerDiems;
    }

    public String getTotalRequestedAllowance() {
        return totalRequestedAllowance;
    }

    @Override
    public String getViewType() {
        return "meal-allowances";
    }
}
