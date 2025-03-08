package gov.nysenate.ess.travel.api.application.statistics;

import java.util.List;

public class TravelStatusStatisticsDTO {

    private String status;
    private int count;
    private String totalExpenses;
    private List<ApplicationEmployeeSummary> statusSummaryList;


    // Constructor
    public TravelStatusStatisticsDTO(String status, int count, String totalExpenses, List<ApplicationEmployeeSummary> statusSummaryList) {
        this.status = status;
        this.count = count;
        this.totalExpenses = totalExpenses;
        this.statusSummaryList = statusSummaryList;
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

    public List<ApplicationEmployeeSummary> getStatusSummaryList() {
        return statusSummaryList;
    }

    public void setStatusSummaryList(List<ApplicationEmployeeSummary> statusSummaryList) {
        this.statusSummaryList = statusSummaryList;
    }
}
