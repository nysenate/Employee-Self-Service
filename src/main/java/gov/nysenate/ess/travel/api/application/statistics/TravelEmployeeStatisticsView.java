package gov.nysenate.ess.travel.api.application.statistics;

import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.List;


public class TravelEmployeeStatisticsView implements ViewObject {

    private Integer emplId;
    private int count;
    private EmployeeView employeeView;
    private String totalExpenses;
    private List<StatusSummary> statusSummaryList;

    // Constructor
    public TravelEmployeeStatisticsView(Integer emplId, EmployeeView employeeView, int count, String totalExpenses, List<StatusSummary> statusSummaryList) {
        this.emplId = emplId;
        this.count = count;
        this.employeeView = employeeView;
        this.totalExpenses = totalExpenses;
        this.statusSummaryList = statusSummaryList;
    }

    public TravelEmployeeStatisticsView(TravelEmployeeStatisticsDTO travelEmployeeStatisticsDTO) {
        emplId = travelEmployeeStatisticsDTO.getEmplId();
        count = travelEmployeeStatisticsDTO.getCount();
        employeeView = travelEmployeeStatisticsDTO.getEmployeeView();
        totalExpenses = travelEmployeeStatisticsDTO.getTotalExpenses();
        statusSummaryList = travelEmployeeStatisticsDTO.getStatusSummaryList();
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

    public EmployeeView getEmployeeView() {
        return employeeView;
    }

    public void setEmployeeView(EmployeeView employeeView) {
        this.employeeView = employeeView;
    }

    @Override
    public String getViewType() {
        return "travel-status-count";
    }
}
