package gov.nysenate.ess.travel.api.application;

import gov.nysenate.ess.core.client.view.base.ViewObject;


public class TravelStatusCountView implements ViewObject {

    private String status;
    private int count;

    // Constructor
    public TravelStatusCountView(String status, int count) {
        this.status = status;
        this.count = count;
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


    @Override
    public String getViewType() {
        return "travel-status-count";
    }
}
