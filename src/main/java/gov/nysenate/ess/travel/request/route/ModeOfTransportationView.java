package gov.nysenate.ess.travel.request.route;

import gov.nysenate.ess.core.client.view.base.ViewObject;

public class ModeOfTransportationView implements ViewObject {

    String methodOfTravel;
    String description;
    String displayName;

    public ModeOfTransportationView() {
    }

    public ModeOfTransportationView(ModeOfTransportation mot) {
        this.methodOfTravel = mot.getMethodOfTravel().toString();
        this.description = mot.getDescription();
        this.displayName = mot.getMethodOfTravel().getDisplayName();
    }

    public ModeOfTransportation toModeOfTransportation() {
        return new ModeOfTransportation(MethodOfTravel.valueOf(methodOfTravel), description);
    }

    public String getMethodOfTravel() {
        return methodOfTravel;
    }

    public String getDescription() {
        return description;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getViewType() {
        return "mode-of-transportation";
    }
}
