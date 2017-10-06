package gov.nysenate.ess.travel.application.view;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.model.ModeOfTransportation;
import gov.nysenate.ess.travel.application.model.TravelDestination;

import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.*;

public class TravelDestinationView implements ViewObject {

    private String arrivalDateTime;
    private String departureDateTime;
    private AddressView address;
    private ModeOfTransportation modeOfTransportation;

    public TravelDestinationView(TravelDestination td) {
        this.arrivalDateTime = td.getArrivalDateTime().format(ISO_DATE_TIME);
        this.departureDateTime = td.getDepartureDateTime().format(ISO_DATE_TIME);
        this.address = new AddressView(td.getAddress());
    }

    public TravelDestination toTravelDestination() {
        return new TravelDestination(LocalDateTime.parse(arrivalDateTime, ISO_DATE_TIME),
                LocalDateTime.parse(departureDateTime, ISO_DATE_TIME),
                address.toAddress(),
                modeOfTransportation);
    }

    public String getArrivalDateTime() {
        return arrivalDateTime;
    }

    public String getDepartureDateTime() {
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
