package gov.nysenate.ess.travel.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.accommodation.AccommodationView;
import gov.nysenate.ess.travel.route.LegView;
import gov.nysenate.ess.travel.route.ModeOfTransportation;
import gov.nysenate.ess.travel.route.RouteView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Combines destination specific information for viewing in the UI.
 * Route and Accommodation are partially derived from a TravelDestination.
 */
public class TravelDestinationView implements ViewObject {

    private String arrivalDate;
    private String departureDate;
    private AddressView address;
    private String modeOfTransportation;
    @JsonProperty(value="isMealsRequested")
    private boolean isMealsRequested;
    @JsonProperty(value="isLodgingRequested")
    private boolean isLodgingRequested;
    @JsonProperty(value="isMileageRequested")
    private boolean isMileageRequested;

    private TravelDestinationView() {
    }

    public TravelDestinationView(AccommodationView accommodation, RouteView route) {
        arrivalDate = accommodation.getArrivalDate();
        departureDate = accommodation.getDepartureDate();
        address = accommodation.getAddress();
        isMealsRequested = accommodation.isMealsRequested();
        isLodgingRequested = accommodation.isLodingRequested();
        Optional<LegView> addressLeg = route.getOutgoingLegs().stream()
                .filter(l -> l.getTo().getFormattedAddress().equals(this.address.getFormattedAddress()))
                .findFirst();
        addressLeg.ifPresent(leg -> modeOfTransportation = leg.getModeOfTransportation());
        addressLeg.ifPresent(leg -> isMileageRequested = leg.isMileageRequested());
    }

    public TravelDestination toTravelDestination() {
        TravelDestination dest = new TravelDestination();
        dest.setArrivalDate(LocalDate.parse(getArrivalDate(), DateTimeFormatter.ISO_DATE));
        dest.setDepartureDate(LocalDate.parse(getDepartureDate(), DateTimeFormatter.ISO_DATE));
        dest.setAddress(getAddress().toAddress());
        dest.setModeOfTransportation(ModeOfTransportation.of(getModeOfTransportation()));
        dest.setMealsRequested(isMealsRequested());
        dest.setLodgingRequested(isLodgingRequested());
        dest.setMileageRequested(isMileageRequested());
        return dest;
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

    public boolean isMealsRequested() {
        return isMealsRequested;
    }

    public boolean isLodgingRequested() {
        return isLodgingRequested;
    }

    public boolean isMileageRequested() {
        return isMileageRequested;
    }

    @Override
    public String getViewType() {
        return "travel-destination";
    }
}
