package gov.nysenate.ess.travel.allowance.mileage.model;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

public class LegView implements ViewObject {

    private AddressView from;
    private AddressView to;

    public LegView() {
    }

    public LegView(Leg leg) {
        this.from = new AddressView(leg.getFrom());
        this.to = new AddressView(leg.getTo());
    }

    public Leg toLeg() {
        return new Leg(from.toAddress(), to.toAddress());
    }

    public AddressView getFrom() {
        return from;
    }

    public AddressView getTo() {
        return to;
    }

    @Override
    public String getViewType() {
        return "leg";
    }
}
