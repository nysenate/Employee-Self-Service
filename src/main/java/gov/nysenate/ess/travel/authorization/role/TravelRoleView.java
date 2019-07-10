package gov.nysenate.ess.travel.authorization.role;

import gov.nysenate.ess.core.client.view.base.ViewObject;

public class TravelRoleView implements ViewObject {

    private String name;
    private String displayName;

    public TravelRoleView() {
    }

    public TravelRoleView(TravelRole role) {
        this.name = role.name();
        this.displayName = role.displayName;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getViewType() {
        return "travel-role";
    }
}
