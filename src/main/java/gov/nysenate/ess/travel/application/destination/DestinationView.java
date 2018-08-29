package gov.nysenate.ess.travel.application.destination;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.address.TravelAddressView;

import java.time.LocalDate;
import java.util.UUID;

import static java.time.format.DateTimeFormatter.*;

public class DestinationView implements ViewObject {

    String id;
    TravelAddressView address;
    String arrivalDate;
    String departureDate;

    public DestinationView() {
    }

    public DestinationView(Destination destination) {
        this.id = destination.getId().toString();
        this.address = new TravelAddressView(destination.getAddress());
        this.arrivalDate = destination.arrivalDate().format(ISO_DATE);
        this.departureDate= destination.departureDate().format(ISO_DATE);
    }

    public Destination toDestination() {
        return new Destination(UUID.fromString(id), address.toTravelAddress(), LocalDate.parse(arrivalDate, ISO_DATE),
                LocalDate.parse(departureDate, ISO_DATE));
    }

    public String getId() {
        return id;
    }

    public AddressView getAddress() {
        return address;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    @Override
    public String getViewType() {
        return "destination";
    }
}
