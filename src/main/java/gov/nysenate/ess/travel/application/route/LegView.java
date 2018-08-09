package gov.nysenate.ess.travel.application.route;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LegView implements ViewObject {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private AddressView from;
    private AddressView to;
    private ModeOfTransportationView modeOfTransportation;
    private String travelDate;

    public LegView() {
    }

    public LegView(Leg leg) {
        this.from = new AddressView(leg.getFrom());
        this.to = new AddressView(leg.getTo());
        this.modeOfTransportation = new ModeOfTransportationView(leg.getModeOfTransportation());
        this.travelDate = leg.getTravelDate().format(DATE_FORMAT);
    }

    public Leg toLeg() {
        return new Leg(from.toAddress(), to.toAddress(),
                modeOfTransportation.toModeOfTransportation(),
                LocalDate.parse(travelDate, DATE_FORMAT));
    }

    public AddressView getFrom() {
        return from;
    }

    public AddressView getTo() {
        return to;
    }

    public ModeOfTransportationView getModeOfTransportation() {
        return modeOfTransportation;
    }

    public String getTravelDate() {
        return travelDate;
    }

    @Override
    public String getViewType() {
        return "route leg";
    }
}
