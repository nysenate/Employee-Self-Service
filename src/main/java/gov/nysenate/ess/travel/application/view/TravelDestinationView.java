package gov.nysenate.ess.travel.application.view;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.model.ModeOfTransportation;
import gov.nysenate.ess.travel.application.model.TravelDestination;

import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ISO_DATE;

public class TravelDestinationView implements ViewObject {

    private String arrivalDateTime;
    private String departureDateTime;
    private AddressView address;
    private ModeOfTransportation modeOfTransportation;

    public TravelDestinationView(TravelDestination td) {
        this.arrivalDateTime = td.getArrivalDate().format(ISO_DATE);
        this.departureDateTime = td.getDepartureDate().format(ISO_DATE);
        this.address = new AddressView(td.getAddress());
    }

    public TravelDestination toTravelDestination() {
        return new TravelDestination(LocalDate.parse(arrivalDateTime, ISO_DATE),
                LocalDate.parse(departureDateTime, ISO_DATE),
                address.toAddress(),
                modeOfTransportation);
    }

    public String getArrivalDate() {
        return arrivalDateTime;
    }

    public String getDepartureDate() {
        return departureDateTime;
    }

    public AddressView getAddress() {
        return address;
    }

    @Override
    public String getViewType() {
        return "travel-destination";
    }
}
