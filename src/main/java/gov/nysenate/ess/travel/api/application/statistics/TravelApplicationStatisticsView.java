package gov.nysenate.ess.travel.api.application.statistics;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.List;

public class TravelApplicationStatisticsView implements ViewObject {

    private int count;
    private String totalExpenses;
    private List<TravelEmployeeStatisticsView> appStatuses;

    public TravelApplicationStatisticsView(int count, String totalExpenses, List<TravelEmployeeStatisticsView> appStatuses) {
        this.totalExpenses = totalExpenses;
        this.appStatuses = appStatuses;
        this.count = count;
    }

    @Override
    public String toString() {
        return "TravelApplicationStatisticsView{" +
                "count=" + count +
                ", totalExpenses=" + totalExpenses +
                ", appStatuses=" + appStatuses +
                '}';
    }

    public String getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(String totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public List<TravelEmployeeStatisticsView> getAppStatuses() {
        return appStatuses;
    }

    public void setAppStatuses(List<TravelEmployeeStatisticsView> appStatuses) {
        this.appStatuses = appStatuses;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String getViewType() {
        return "travel_application_statistics";
    }
}
