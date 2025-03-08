package gov.nysenate.ess.travel.api.application.statistics;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.List;

public class TravelApplicationStatisticsByStatusView implements ViewObject {

    private final int count;
    private final String totalExpenses;
    private final List<TravelStatusStatisticsView> travelStatusStatisticsViews;

    public TravelApplicationStatisticsByStatusView(int count, String totalExpenses, List<TravelStatusStatisticsView> travelStatusStatisticsViews) {
        this.totalExpenses = totalExpenses;
        this.travelStatusStatisticsViews = travelStatusStatisticsViews;
        this.count = count;
    }

    @Override
    public String toString() {
        return "TravelApplicationStatisticsByStatusView{" +
                "count=" + count +
                ", totalExpenses=" + totalExpenses +
                ", appStatuses=" + travelStatusStatisticsViews +
                '}';
    }

    @Override
    public String getViewType() {
        return "travel_application_statistics";
    }
}
