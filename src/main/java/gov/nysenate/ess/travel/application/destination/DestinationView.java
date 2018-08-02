package gov.nysenate.ess.travel.application.destination;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.*;

public class DestinationView implements ViewObject {

    AddressView address;
    String arrivalDate;
    String departureDate;

    public DestinationView() {
    }

    public DestinationView(Destination destination) {
        this.address = new AddressView(destination.getAddress());
        this.arrivalDate = destination.arrivalDate().format(ISO_DATE);
        this.departureDate= destination.departureDate().format(ISO_DATE);
    }

    public Destination toDestination() {
        return new Destination(address.toAddress(), LocalDate.parse(arrivalDate, ISO_DATE),
                LocalDate.parse(departureDate, ISO_DATE));
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
