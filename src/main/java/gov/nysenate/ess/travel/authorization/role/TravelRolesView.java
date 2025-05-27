package gov.nysenate.ess.travel.authorization.role;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.List;
import java.util.stream.Collectors;

public class TravelRolesView implements ViewObject {

    private List<TravelRoleView> allRoles;
    private List<TravelRoleView> primary;
    private List<TravelRoleView> delegate;

    public TravelRolesView() {
    }

    public TravelRolesView(TravelRoles roles) {
        this.primary = roles.primary().stream()
                .map(TravelRoleView::new)
                .collect(Collectors.toList());
        this.delegate = roles.delegate().stream()
                .map(TravelRoleView::new)
                .collect(Collectors.toList());
        this.allRoles = roles.all().stream()
                .map(TravelRoleView::new)
                .collect(Collectors.toList());
    }

    public List<TravelRoleView> getAllRoles() {
        return allRoles;
    }

    public List<TravelRoleView> getPrimary() {
        return primary;
    }

    public List<TravelRoleView> getDelegate() {
        return delegate;
    }

    @Override
    public String getViewType() {
        return "travel-roles";
    }
}
