package gov.nysenate.ess.travel.api.application.statistics;

import gov.nysenate.ess.core.client.view.EmployeeView;

import java.util.List;

public class TravelEmployeeStatisticsDTO {

    private Integer empId;
    private EmployeeView employeeView;
    private int count;
    private String totalExpenses;
    private List<ApplicationStatusSummary> applicationStatusSummaryList;


    // Constructor
    public TravelEmployeeStatisticsDTO(Integer empId, EmployeeView employeeView, int count, String totalExpenses, List<ApplicationStatusSummary> applicationStatusSummaryList) {
        this.empId = empId;
        this.count = count;
        this.employeeView = employeeView;
        this.totalExpenses = totalExpenses;
        this.applicationStatusSummaryList = applicationStatusSummaryList;
    }

    // Getters and Setters
    public Integer getEmplId() {
        return empId;
    }

    public void setEmplId(Integer empId) {
        this.empId = empId;
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

    public String getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(String totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public List<ApplicationStatusSummary> getStatusSummaryList() {
        return applicationStatusSummaryList;
    }

    public void setStatusSummaryList(List<ApplicationStatusSummary> applicationStatusSummaryList) {
        this.applicationStatusSummaryList = applicationStatusSummaryList;
    }
}
