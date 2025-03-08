package gov.nysenate.ess.travel.api.application.statistics;

import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.List;


public class TravelEmployeeStatisticsView implements ViewObject {

    private Integer empId;
    private int count;
    private EmployeeView employeeView;
    private String totalExpenses;
    private List<ApplicationStatusSummary> applicationStatusSummaryList;

    // Constructor
    public TravelEmployeeStatisticsView(Integer empId, EmployeeView employeeView, int count, String totalExpenses, List<ApplicationStatusSummary> applicationStatusSummaryList) {
        this.empId = empId;
        this.count = count;
        this.employeeView = employeeView;
        this.totalExpenses = totalExpenses;
        this.applicationStatusSummaryList = applicationStatusSummaryList;
    }

    public TravelEmployeeStatisticsView(TravelEmployeeStatisticsDTO travelEmployeeStatisticsDTO) {
        empId = travelEmployeeStatisticsDTO.getEmplId();
        count = travelEmployeeStatisticsDTO.getCount();
        employeeView = travelEmployeeStatisticsDTO.getEmployeeView();
        totalExpenses = travelEmployeeStatisticsDTO.getTotalExpenses();
        applicationStatusSummaryList = travelEmployeeStatisticsDTO.getStatusSummaryList();
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

    public EmployeeView getEmployeeView() {
        return employeeView;
    }

    public void setEmployeeView(EmployeeView employeeView) {
        this.employeeView = employeeView;
    }

    @Override
    public String getViewType() {
        return "travel_employee_statistics";
    }
}
