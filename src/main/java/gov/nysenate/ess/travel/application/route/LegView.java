package gov.nysenate.ess.travel.application.route;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.address.TravelAddressView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class LegView implements ViewObject {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private String id;
    private TravelAddressView from;
    private TravelAddressView to;
    private ModeOfTransportationView modeOfTransportation;
    private String travelDate;

    public LegView() {
    }

    public LegView(Leg leg) {
        this.id = leg.getId().toString();
        this.from = new TravelAddressView(leg.getFrom());
        this.to = new TravelAddressView(leg.getTo());
        this.modeOfTransportation = new ModeOfTransportationView(leg.getModeOfTransportation());
        this.travelDate = leg.getTravelDate().format(DATE_FORMAT);
    }

    public Leg toLeg() {
        // TODO
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        return new Leg(UUID.fromString(id), from.toTravelAddress(), to.toTravelAddress(),
                modeOfTransportation.toModeOfTransportation(),
                LocalDate.parse(travelDate, DATE_FORMAT));
    }

    public String getId() {
        return id;
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
