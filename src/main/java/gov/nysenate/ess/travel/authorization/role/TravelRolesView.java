package gov.nysenate.ess.travel.authorization.role;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.List;
import java.util.stream.Collectors;

public class TravelRolesView implements ViewObject {

    private List<TravelRoleView> roles;

    public TravelRolesView() {
    }

    public TravelRolesView(TravelRoles roles) {
        this.roles = roles.all().stream()
                .map(TravelRoleView::new)
                .collect(Collectors.toList());
    }

    public List<TravelRoleView> getRoles() {
        return roles;
    }

    @Override
    public String getViewType() {
        return "travel-roles";
    }
}
