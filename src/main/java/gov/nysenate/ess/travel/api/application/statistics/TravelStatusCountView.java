package gov.nysenate.ess.travel.api.application.statistics;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.api.application.TravelApplicationView;
import gov.nysenate.ess.travel.request.app.TravelApplication;

import java.util.List;


public class TravelStatusCountView implements ViewObject {

    private String status;
    private int count;
    private String totalExpenses;
    private List<TravelApplicationView> travelApplications;

    // Constructor
    public TravelStatusCountView(String status, int count, String totalExpenses, List<TravelApplicationView> travelApplications) {
        this.status = status;
        this.count = count;
        this.totalExpenses = totalExpenses;
        this.travelApplications = travelApplications;
    }

    public TravelStatusCountView(TravelStatusCountDTO travelStatusCountDTO) {
        status = travelStatusCountDTO.getStatus();
        count = travelStatusCountDTO.getCount();
        totalExpenses = travelStatusCountDTO.getTotalExpenses();
        travelApplications = travelStatusCountDTO.getTravelApplicationList();
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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

    @Override
    public String getViewType() {
        return "travel-status-count";
    }
}
