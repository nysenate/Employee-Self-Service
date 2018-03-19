package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.route.ModeOfTransportation;

import java.time.LocalDate;

public class TravelDestination {

    private LocalDate arrivalDate;
    private LocalDate departureDate;
    private Address address;
    private ModeOfTransportation modeOfTransportation;
    private boolean isMealsRequested;
    private boolean isLodgingRequested;

    public TravelDestination() {
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public ModeOfTransportation getModeOfTransportation() {
        return modeOfTransportation;
    }

    public void setModeOfTransportation(ModeOfTransportation modeOfTransportation) {
        this.modeOfTransportation = modeOfTransportation;
    }

    public boolean isMealsRequested() {
        return isMealsRequested;
    }

    public void setMealsRequested(boolean mealsRequested) {
        isMealsRequested = mealsRequested;
    }

    public boolean isLodgingRequested() {
        return isLodgingRequested;
    }

    public void setLodgingRequested(boolean lodgingRequested) {
        isLodgingRequested = lodgingRequested;
    }
}
