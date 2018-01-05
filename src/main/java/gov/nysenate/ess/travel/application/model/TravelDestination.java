package gov.nysenate.ess.travel.application.model;

import gov.nysenate.ess.core.model.unit.Address;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a single destination in a travel request.
 * Each destination requires an address, arrival, and departure time
 * to be used in GSA meal and lodging allowance calculations.
 */
public final class TravelDestination {

    private final LocalDate arrivalDate;
    private final LocalDate departureDate;
    private final Address address;
    private final ModeOfTransportation modeOfTransportation;
    private final boolean isWaypoint;

    public TravelDestination(LocalDate arrivalDate, LocalDate departureDate, Address address,
                             ModeOfTransportation modeOfTransportation) {
        this(arrivalDate, departureDate, address, modeOfTransportation, false);
    }

    public TravelDestination(LocalDate arrivalDate, LocalDate departureDate, Address address,
                             ModeOfTransportation modeOfTransportation, boolean isWaypoint) {
        checkNotNull(arrivalDate);
        checkNotNull(departureDate);
        checkNotNull(address);
        checkArgument(!address.isEmpty());
        checkArgument(!departureDate.isBefore(arrivalDate));
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.address = address;
        this.modeOfTransportation = modeOfTransportation;
        this.isWaypoint = isWaypoint;
    }

    /**
     * @return A List containing all dates at this location.
     */
    public List<LocalDate> getDatesOfStay() {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate d = getArrivalDate();
        while(!d.isAfter(getDepartureDate())) {
            dates.add(d);
            d = d.plusDays(1);
        }
        return dates;
    }

    /**
     * @return A Set of the nights at this location.
     */
    public Set<LocalDate> getNightsOfStay() {
        return getDatesOfStay().stream().skip(1).collect(Collectors.toSet());
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

    public ModeOfTransportation getModeOfTransportation() {
        return modeOfTransportation;
    }

    public boolean isWaypoint() {
        return isWaypoint;
    }
}
