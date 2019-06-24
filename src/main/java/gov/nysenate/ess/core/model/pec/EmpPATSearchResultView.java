package gov.nysenate.ess.core.model.pec;

import gov.nysenate.ess.core.client.view.DetailedEmployeeView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.client.view.pec.PersonnelAssignedTaskView;
import gov.nysenate.ess.core.service.pec.EmployeeTaskSearchResult;

import java.util.List;
import java.util.stream.Collectors;

public class EmpPATSearchResultView implements ViewObject {

    private DetailedEmployeeView employee;
    private List<PersonnelAssignedTaskView> tasks;

    public EmpPATSearchResultView(EmployeeTaskSearchResult result) {
        this.employee = new DetailedEmployeeView(result.getEmployee());
        this.tasks = result.getTasks().stream()
                .map(PersonnelAssignedTaskView::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getViewType() {
        return "emp-pat-search-result";
    }

    public DetailedEmployeeView getEmployee() {
        return employee;
    }

    public List<PersonnelAssignedTaskView> getTasks() {
        return tasks;
    }
}
