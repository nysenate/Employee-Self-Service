package gov.nysenate.ess.travel.api.application.statistics;

import gov.nysenate.ess.core.client.view.EmployeeView;

import java.util.List;

public class TravelStatusCountDTO {

    private Integer emplId;
    private EmployeeView employeeView;
    private int count;
    private String totalExpenses;
    private List<StatusSummary> statusSummaryList;


    // Constructor
    public TravelStatusCountDTO(Integer emplId, EmployeeView employeeView, int count, String totalExpenses, List<StatusSummary> statusSummaryList) {
        this.emplId = emplId;
        this.count = count;
        this.employeeView = employeeView;
        this.totalExpenses = totalExpenses;
        this.statusSummaryList = statusSummaryList;
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

    public EmployeeView getEmployeeView() {
        return employeeView;
    }

    public void setEmployeeView(EmployeeView employeeView) {
        this.employeeView = employeeView;
    }

    public String getTotalExpenses() { return totalExpenses; }

    public void setTotalExpenses(String totalExpenses) {
        this.totalExpenses = totalExpenses;
    }
    public List<StatusSummary> getStatusSummaryList() { return statusSummaryList; }

    public void setStatusSummaryList(List<StatusSummary> statusSummaryList) {
        this.statusSummaryList = statusSummaryList;
    }
}
