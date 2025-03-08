package gov.nysenate.ess.travel.api.application.statistics;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.List;

public class TravelApplicationStatisticsByEmployeeView implements ViewObject {

    private final int count;
    private final String totalExpenses;
    private final List<TravelEmployeeStatisticsView> travelEmployeeStatisticsViews;

    public TravelApplicationStatisticsByEmployeeView(int count, String totalExpenses, List<TravelEmployeeStatisticsView> appStatuses) {
        this.totalExpenses = totalExpenses;
        this.travelEmployeeStatisticsViews = appStatuses;
        this.count = count;
    }

    @Override
    public String toString() {
        return "TravelApplicationStatisticsByEmployeeView{" +
                "count=" + count +
                ", totalExpenses=" + totalExpenses +
                ", appStatuses=" + travelEmployeeStatisticsViews +
                '}';
    }

    @Override
    public String getViewType() {
        return "travel_application_statistics";
    }
}
