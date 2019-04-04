package gov.nysenate.ess.travel.application.allowances.meal;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.List;
import java.util.stream.Collectors;

public class MealAllowancesView implements ViewObject {

    private List<MealPerDiemView> allMealPerDiems;
    private List<MealPerDiemView> requestedMealPerDiems;
    private String requestedAllowance;
    private String maximumAllowance;

    public MealAllowancesView() {
    }

    public MealAllowancesView(MealAllowances ma) {
        this.allMealPerDiems = ma.allMealPerDiems().stream()
                .map(MealPerDiemView::new)
                .collect(Collectors.toList());
        this.requestedMealPerDiems = ma.requestedMealPerDiems().stream()
                .map(MealPerDiemView::new)
                .collect(Collectors.toList());
        this.requestedAllowance = ma.requestedAllowance().toString();
        this.maximumAllowance = ma.maximumAllowance().toString();
    }

    public List<MealPerDiemView> getAllMealPerDiems() {
        return allMealPerDiems;
    }

    public List<MealPerDiemView> getRequestedMealPerDiems() {
        return requestedMealPerDiems;
    }

    public String getRequestedAllowance() {
        return requestedAllowance;
    }

    public String getMaximumAllowance() {
        return maximumAllowance;
    }

    @Override
    public String getViewType() {
        return "meal-allowances";
    }
}
