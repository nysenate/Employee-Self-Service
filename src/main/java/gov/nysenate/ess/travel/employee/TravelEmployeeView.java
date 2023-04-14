package gov.nysenate.ess.travel.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.department.DepartmentView;

public class TravelEmployeeView extends EmployeeView implements ViewObject {

    @JsonProperty("isDepartmentHead")
    private boolean isDepartmentHead;
    private DepartmentView department;

    public TravelEmployeeView(TravelEmployee travelEmployee) {
        super(travelEmployee);
        this.isDepartmentHead = travelEmployee.isDepartmentHead();
        this.department = new DepartmentView(travelEmployee.getDepartment());
    }

    @JsonIgnore
    public boolean isDepartmentHead() {
        return isDepartmentHead;
    }

    public DepartmentView getDepartment() {
        return department;
    }

    @Override
    public String getViewType() {
        return "travel-employee";
    }
}
