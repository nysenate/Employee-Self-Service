package gov.nysenate.ess.travel.api.application.statistics;

import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.travel.api.application.TravelApplicationView;

import java.util.List;

public class ApplicationEmployeeSummary {

    private int empId;
    private int count;
    private String totalExpenses;
    private EmployeeView employeeView;
    private List<TravelApplicationView> travelApplications;

    public ApplicationEmployeeSummary(int empId, int count, String totalExpenses, EmployeeView employeeView, List<TravelApplicationView> travelApplications) {
        this.empId = empId;
        this.count = count;
        this.employeeView = employeeView;
        this.totalExpenses = totalExpenses;
        this.travelApplications = travelApplications;
    }

    public EmployeeView getEmployeeView() {
        return employeeView;
    }

    public void setEmployeeView(EmployeeView employeeView) {
        this.employeeView = employeeView;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getEmplId() {
        return empId;
    }

    public void setEmplId(int empId) {
        this.empId = empId;
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
}
