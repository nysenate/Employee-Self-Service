package gov.nysenate.ess.travel.route;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

public class LegView implements ViewObject {

    private final AddressView from;
    private final AddressView to;
    private final String miles;

    public LegView(Leg leg) {
        this.from = new AddressView(leg.getFrom());
        this.to = new AddressView(leg.getTo());
        this.miles = String.valueOf(leg.getMiles());
    }

    public AddressView getFrom() {
        return from;
    }

    public AddressView getTo() {
        return to;
    }

    public String getMiles() {
        return miles;
    }

    @Override
    public String getViewType() {
        return "route leg";
    }
}
