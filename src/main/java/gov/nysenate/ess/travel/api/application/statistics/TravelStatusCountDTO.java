package gov.nysenate.ess.travel.api.application.statistics;

import gov.nysenate.ess.travel.api.application.TravelApplicationView;
import gov.nysenate.ess.travel.request.app.TravelApplication;
import java.util.List;

public class TravelStatusCountDTO {

    private String status;
    private int count;
    private String totalExpenses;
    private List<TravelApplicationView> travelApplicationList;

    // Constructor
    public TravelStatusCountDTO(String status, int count, String totalExpenses, List<TravelApplicationView> travelApplicationList) {
        this.status = status;
        this.count = count;
        this.totalExpenses = totalExpenses;
        this.travelApplicationList = travelApplicationList;
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

    public String getTotalExpenses() { return totalExpenses; }

    public void setTotalExpenses(String totalExpenses) {
        this.totalExpenses = totalExpenses;
    }
    public List<TravelApplicationView> getTravelApplicationList() { return travelApplicationList; }

    public void setTravelApplicationList(List<TravelApplicationView> travelApplicationList) {
        this.travelApplicationList = travelApplicationList;
    }
}
