package gov.nysenate.ess.travel.application.model;

import gov.nysenate.ess.core.model.unit.Address;

import java.time.LocalDate;

/**
 * Represents a single destination in a travel request.
 * Each destination requires an address, arrival, and departure time
 * to be used in GSA meal and lodging allowance calculations.
 */
public class TravelDestination {

    private LocalDate arrivalDate;
    private LocalDate departureDate;
    private Address address;

    public TravelDestination(LocalDate arrivalDate, LocalDate departureDate,
                             Address address) {
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.address = address;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public Address getAddress() {
        return address;
    }
}
