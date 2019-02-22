package gov.nysenate.ess.supply.destination;

import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.client.view.ErrorCodeView;
import gov.nysenate.ess.core.client.view.LocationView;
import gov.nysenate.ess.core.client.view.base.ListView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;

import java.util.Set;
import java.util.stream.Collectors;

public class DestinationsView implements ViewObject {

    private EmployeeView employee;
    private ListView<LocationView> destinations;
    private ListView<ErrorCodeView> errorCodes;

    public DestinationsView(Employee employee, Set<Location> destinations, Set<ErrorCode> errorCodes) {
        this.employee = new EmployeeView(employee);
        this.destinations = ListView.of(destinations.stream().map(LocationView::new).collect(Collectors.toList()));
        this.errorCodes = ListView.of(errorCodes.stream().map(ErrorCodeView::new).collect(Collectors.toList()));
    }

    public EmployeeView getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeView employee) {
        this.employee = employee;
    }

    public ListView<LocationView> getDestinations() {
        return destinations;
    }

    public void setDestinations(ListView<LocationView> destinations) {
        this.destinations = destinations;
    }

    public ListView<ErrorCodeView> getErrorCodes() {
        return errorCodes;
    }

    public void setErrorCodes(ListView<ErrorCodeView> errorCodes) {
        this.errorCodes = errorCodes;
    }

    @Override
    public String getViewType() {
        return "destinations-view";
    }
}
