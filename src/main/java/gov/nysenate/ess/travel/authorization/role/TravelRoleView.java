package gov.nysenate.ess.travel.authorization.role;

import gov.nysenate.ess.core.client.view.base.ViewObject;

public class TravelRoleView implements ViewObject {

    private String name;
    private String displayName;
    private boolean canViewShared;

    public TravelRoleView() {
    }

    public TravelRoleView(TravelRole role) {
        this.name = role.name();
        this.displayName = role.displayName;
        this.canViewShared = role.canViewShared;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isCanViewShared() {
        return canViewShared;
    }

    @Override
    public String getViewType() {
        return "travel-role";
    }

    /**
     * This is called when used as a key in a MapView. In this situation, just use the name string.
     * @return
     */
    @Override
    public String toString() {
        return this.name;
    }
}
