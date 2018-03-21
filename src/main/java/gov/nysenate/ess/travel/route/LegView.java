package gov.nysenate.ess.travel.route;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

public class LegView implements ViewObject {

    private AddressView from;
    private AddressView to;
    private String miles;
    private String modeOfTransportation;
    private boolean qualifies; // Does this leg qualify for reimbursement.

    public LegView() {
    }

    public LegView(Leg leg) {
        this.from = new AddressView(leg.getFrom());
        this.to = new AddressView(leg.getTo());
        this.miles = String.valueOf(leg.getMiles());
        this.modeOfTransportation = leg.getModeOfTransportation().getDisplayName();
        this.qualifies = leg.qualifies();
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

    public String getModeOfTransportation() {
        return modeOfTransportation;
    }

    public boolean isQualifies() {
        return qualifies;
    }

    @Override
    public String getViewType() {
        return "route leg";
    }
}
