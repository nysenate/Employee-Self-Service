package gov.nysenate.ess.travel.api.application;

import gov.nysenate.ess.core.client.view.DetailedEmployeeView;
import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.EventType;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TravelAppEditDto implements ViewObject {

    // This field can be used to update the application traveler when creating a new travel application.
    private DetailedEmployeeView traveler;
    private int travelerDeptHeadEmpId;
    // Apply Amendment that is currently being edited.
    private AmendmentView amendment;

    // Set of employees the user is allowed to submit apps for.
    private Set<EmployeeView> allowedTravelers;
    private Set<EmployeeView> possibleDepartmentHeads;
    // All Possible event types for the Purpose of Travel
    private List<EventTypeView> validEventTypes;

    public TravelAppEditDto() {
    }

    public TravelAppEditDto(DetailedEmployeeView traveler, AmendmentView amendmentView, int travelerDeptHeadEmpId) {
        this.traveler = traveler;
        this.amendment = amendmentView;
        this.travelerDeptHeadEmpId = travelerDeptHeadEmpId;
        this.validEventTypes = EnumSet.allOf(EventType.class).stream().map(EventTypeView::new).collect(Collectors.toList());
    }

    public void setAllowedTravelers(Set<Employee> allowedTravelers) {
        this.allowedTravelers = allowedTravelers.stream()
                .map(EmployeeView::new)
                .collect(Collectors.toSet());
    }

    public DetailedEmployeeView getTraveler() {
        return traveler;
    }

    public int getTravelerDeptHeadEmpId() {
        return travelerDeptHeadEmpId;
    }

    public AmendmentView getAmendment() {
        return amendment;
    }

    public Set<EmployeeView> getAllowedTravelers() {
        return allowedTravelers;
    }

    public void setTraveler(DetailedEmployeeView traveler) {
        this.traveler = traveler;
    }

    public void setTravelerDeptHeadEmpId(int travelerDeptHeadEmpId) {
        this.travelerDeptHeadEmpId = travelerDeptHeadEmpId;
    }

    public void setAmendment(AmendmentView amendment) {
        this.amendment = amendment;
    }

    public List<EventTypeView> getValidEventTypes() {
        return validEventTypes;
    }

    public Set<EmployeeView> getPossibleDepartmentHeads() {
        return possibleDepartmentHeads;
    }

    public void setPossibleDepartmentHeads(Set<EmployeeView> possibleDepartmentHeads) {
        this.possibleDepartmentHeads = possibleDepartmentHeads;
    }

    @Override
    public String getViewType() {
        return "travel-app-edit-dto";
    }
}