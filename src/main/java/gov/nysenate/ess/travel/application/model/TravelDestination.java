package gov.nysenate.ess.travel.application.model;

import gov.nysenate.ess.core.model.unit.Address;

import java.time.LocalDateTime;

/**
 * Represents a single destination in a travel request.
 * Each destination requires an address, arrival, and departure time
 * to be used in GSA meal and lodging allowance calculations.
 */
public class TravelDestination {

    private LocalDateTime arrivalDateTime;
    private LocalDateTime departureDateTime;
    private Address address;

    public TravelDestination(LocalDateTime arrivalDateTime, LocalDateTime departureDateTime, Address address) {
        this.arrivalDateTime = arrivalDateTime;
        this.departureDateTime = departureDateTime;
        this.address = address;
    }

    public LocalDateTime getArrivalDateTime() {
        return arrivalDateTime;
    }

    public LocalDateTime getDepartureDateTime() {
        return departureDateTime;
    }

    public Address getAddress() {
        return address;
    }
}
