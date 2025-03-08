package gov.nysenate.ess.travel.api.application.statistics;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.List;

public class TravelStatusStatisticsView implements ViewObject {

    private String status;
    private int count;
    private String totalExpenses;
    private List<ApplicationEmployeeSummary> applicationEmployeeSummaryList;


    // Constructor
    public TravelStatusStatisticsView(String status, int count, String totalExpenses, List<ApplicationEmployeeSummary> applicationEmployeeSummaryList) {
        this.status = status;
        this.count = count;
        this.totalExpenses = totalExpenses;
        this.applicationEmployeeSummaryList = applicationEmployeeSummaryList;
    }

    public TravelStatusStatisticsView(TravelStatusStatisticsDTO travelStatusStatisticsDTO) {
        status = travelStatusStatisticsDTO.getStatus();
        count = travelStatusStatisticsDTO.getCount();
        totalExpenses = travelStatusStatisticsDTO.getTotalExpenses();
        applicationEmployeeSummaryList = travelStatusStatisticsDTO.getStatusSummaryList();
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

    public List<ApplicationEmployeeSummary> getApplicationEmployeeSummaryList() {
        return applicationEmployeeSummaryList;
    }

    public void setApplicationEmployeeSummaryList(List<ApplicationEmployeeSummary> applicationEmployeeSummaryList) {
        this.applicationEmployeeSummaryList = applicationEmployeeSummaryList;
    }

    @Override
    public String getViewType() {
        return "travel_status_statistics";
    }
}
