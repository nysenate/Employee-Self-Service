package gov.nysenate.ess.travel.api.application.statistics;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.api.application.TravelApplicationView;

import java.util.List;
import java.util.Map;


public class TravelStatusCountView implements ViewObject {

    private Integer emplId;
    private int count;
    private String totalExpenses;
    private List<StatusSummary> statusSummaryList;

    // Constructor
    public TravelStatusCountView(Integer emplId, int count, String totalExpenses, List<StatusSummary> statusSummaryList) {
        this.emplId = emplId;
        this.count = count;
        this.totalExpenses = totalExpenses;
        this.statusSummaryList = statusSummaryList;
    }

    public TravelStatusCountView(TravelStatusCountDTO travelStatusCountDTO) {
        emplId = travelStatusCountDTO.getEmplId();
        count = travelStatusCountDTO.getCount();
        totalExpenses = travelStatusCountDTO.getTotalExpenses();
        statusSummaryList = travelStatusCountDTO.getStatusSummaryList();
    }

    // Getters and Setters
    public Integer getEmplId() {
        return emplId;
    }

    public void setEmplId(Integer emplId) {
        this.emplId = emplId;
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

    public List<StatusSummary> getStatusSummaryList() { return statusSummaryList; }

    public void setStatusSummaryList(List<StatusSummary> statusSummaryList) {
        this.statusSummaryList = statusSummaryList;
    }

    @Override
    public String getViewType() {
        return "travel-status-count";
    }
}
