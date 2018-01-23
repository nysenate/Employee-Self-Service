package gov.nysenate.ess.travel.application.view;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.model.ModeOfTransportation;
import gov.nysenate.ess.travel.application.model.TravelDestination;
import gov.nysenate.ess.travel.application.model.TravelDestinationOptions;

import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ISO_DATE;

public class TravelDestinationView implements ViewObject {

    private String arrivalDate;
    private String departureDate;
    private AddressView address;
    private String modeOfTransportation;
    private boolean requestMeals;
    private boolean requestLodging;
    private boolean requestMileage;

    private TravelDestinationView() {
    }

    public TravelDestinationView(Map.Entry<TravelDestination, TravelDestinationOptions> destToOptionsEntry) {
        TravelDestination destination = destToOptionsEntry.getKey();
        TravelDestinationOptions options = destToOptionsEntry.getValue();
        this.arrivalDate = destination.getArrivalDate().format(ISO_DATE);
        this.departureDate = destination.getDepartureDate().format(ISO_DATE);
        this.address = new AddressView(destination.getAddress());
        this.modeOfTransportation = options.getModeOfTransportation().toString();
        this.requestMeals = options.isRequestMeals();
        this.requestLodging = options.isRequestLodging();
        this.requestMileage = options.isRequestMileage();
    }

    public Map.Entry<TravelDestination, TravelDestinationOptions> toDestinationOptionsEntry() {
        TravelDestination destination = new TravelDestination(LocalDate.parse(arrivalDate, ISO_DATE),
                LocalDate.parse(departureDate, ISO_DATE), address.toAddress());
        TravelDestinationOptions options = new TravelDestinationOptions(ModeOfTransportation.of(modeOfTransportation),
                requestMeals, requestLodging, requestMileage);
        return new AbstractMap.SimpleEntry<>(destination, options);
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

    public boolean isRequestMeals() {
        return requestMeals;
    }

    public boolean isRequestLodging() {
        return requestLodging;
    }

    public boolean isRequestMileage() {
        return requestMileage;
    }

    @Override
    public String getViewType() {
        return "travel-destination";
    }
}
