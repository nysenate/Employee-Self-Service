package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.route.ModeOfTransportation;

import java.time.LocalDate;

public class Segment {

    private Address from;
    private Address to;
    private LocalDate departureDate;
    private LocalDate arrivalDate;
    private ModeOfTransportation modeOfTransportation;
    private boolean isMileageRequested;
    private boolean isMealsRequested;
    private boolean isLodgingRequested;

    public Address getFrom() {
        return from;
    }

    public void setFrom(Address from) {
        this.from = from;
    }

    public Address getTo() {
        return to;
    }

    public void setTo(Address to) {
        this.to = to;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public ModeOfTransportation getModeOfTransportation() {
        return modeOfTransportation;
    }

    public void setModeOfTransportation(ModeOfTransportation modeOfTransportation) {
        this.modeOfTransportation = modeOfTransportation;
    }

    public boolean isMileageRequested() {
        return isMileageRequested;
    }

    public void setMileageRequested(boolean mileageRequested) {
        isMileageRequested = mileageRequested;
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
