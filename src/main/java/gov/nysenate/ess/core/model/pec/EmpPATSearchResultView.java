package gov.nysenate.ess.core.model.pec;

import gov.nysenate.ess.core.client.view.DetailedEmployeeView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.client.view.pec.PersonnelTaskAssignmentView;
import gov.nysenate.ess.core.service.pec.search.EmployeeTaskSearchResult;

import java.util.List;
import java.util.stream.Collectors;

public class EmpPATSearchResultView implements ViewObject {

    private DetailedEmployeeView employee;
    private List<PersonnelTaskAssignmentView> tasks;

    public EmpPATSearchResultView(EmployeeTaskSearchResult result) {
        this.employee = new DetailedEmployeeView(result.getEmployee());
        this.tasks = result.getTasks().stream()
                .map(PersonnelTaskAssignmentView::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getViewType() {
        return "emp-pat-search-result";
    }

    public DetailedEmployeeView getEmployee() {
        return employee;
    }

    public List<PersonnelTaskAssignmentView> getTasks() {
        return tasks;
    }
}
