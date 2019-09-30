package gov.nysenate.ess.travel.application.unsubmitted;

import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.application.TravelApplicationView;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class NewApplicationDto implements ViewObject {

    private TravelApplicationView app;
    // Employees the logged in user is allowed to submit a travel application for.
    private List<EmployeeView> allowedTravelers;

    public NewApplicationDto() {
    }

    public NewApplicationDto(TravelApplicationView view, Collection<Employee> employees) {
        this.app = view;
        this.allowedTravelers = employees.stream().map(EmployeeView::new).collect(Collectors.toList());
    }

    public TravelApplicationView getApp() {
        return app;
    }

    public List<EmployeeView> getAllowedTravelers() {
        return allowedTravelers;
    }

    @Override
    public String getViewType() {
        return "new-application-dto";
    }
}
