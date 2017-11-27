package gov.nysenate.ess.travel.application.view;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.model.ModeOfTransportation;
import gov.nysenate.ess.travel.application.model.TravelDestination;

import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ISO_DATE;

public class TravelDestinationView implements ViewObject {

    private String arrivalDate;
    private String departureDate;
    private AddressView address;
    private String modeOfTransportation;
    private boolean isWaypoint;

    private TravelDestinationView() {
    }

    public TravelDestinationView(TravelDestination td) {
        this.arrivalDate = td.getArrivalDate().format(ISO_DATE);
        this.departureDate = td.getDepartureDate().format(ISO_DATE);
        this.address = new AddressView(td.getAddress());
        this.modeOfTransportation = td.getModeOfTransportation().name();
        this.isWaypoint = td.isWaypoint();
    }

    public TravelDestination toTravelDestination() {
        return new TravelDestination(LocalDate.parse(arrivalDate, ISO_DATE), LocalDate.parse(departureDate, ISO_DATE),
                address.toAddress(), ModeOfTransportation.of(modeOfTransportation), isWaypoint);
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public AddressView getAddress() {
        return address;
    }

    public String getModeOfTransportation() {
        return modeOfTransportation;
    }

    public boolean isWaypoint() {
        return isWaypoint;
    }

    @Override
    public String getViewType() {
        return "travel-destination";
    }
}
