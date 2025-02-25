package gov.nysenate.ess.travel.api.application.statistics;

import gov.nysenate.ess.travel.api.application.TravelApplicationView;

import java.util.List;

public class StatusSummary {

    private String status;
    private int count;
    private String totalExpenses;
    private List<TravelApplicationView> travelApplications;

    public StatusSummary(String status, int count, String totalExpenses, List<TravelApplicationView> travelApplications) {
        this.status = status;
        this.count = count;
        this.totalExpenses = totalExpenses;
        this.travelApplications = travelApplications;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(String totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public List<TravelApplicationView> getTravelApplications() {
        return travelApplications;
    }

    public void setTravelApplications(List<TravelApplicationView> travelApplications) {
        this.travelApplications = travelApplications;
    }
}
