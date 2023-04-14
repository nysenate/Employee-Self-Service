package gov.nysenate.ess.travel.department;

import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.Set;
import java.util.stream.Collectors;

public class DepartmentView implements ViewObject {

    private EmployeeView head;
    private Set<EmployeeView> subordinates;

    public DepartmentView(Department department) {
        this.head = new EmployeeView(department.getHead());
        this.subordinates = department.getSubordinates().stream()
                .map(EmployeeView::new)
                .collect(Collectors.toSet());
    }

    public EmployeeView getHead() {
        return head;
    }

    public Set<EmployeeView> getSubordinates() {
        return subordinates;
    }

    @Override
    public String getViewType() {
        return "department";
    }
}
