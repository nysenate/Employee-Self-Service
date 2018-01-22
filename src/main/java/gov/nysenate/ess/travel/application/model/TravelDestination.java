package gov.nysenate.ess.travel.application.model;

import gov.nysenate.ess.core.model.unit.Address;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a single destination in a travel request.
 */
public final class TravelDestination {

    private final LocalDate arrivalDate;
    private final LocalDate departureDate;
    private final Address address;

    public TravelDestination(LocalDate arrivalDate, LocalDate departureDate, Address address) {
        checkNotNull(arrivalDate);
        checkNotNull(departureDate);
        checkNotNull(address);
        checkArgument(!address.isEmpty());
        checkArgument(!departureDate.isBefore(arrivalDate));
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.address = address;
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

    @Override
    public String toString() {
        return "TravelDestination{" +
                "arrivalDate=" + arrivalDate +
                ", departureDate=" + departureDate +
                ", address=" + address +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TravelDestination that = (TravelDestination) o;
        return Objects.equals(arrivalDate, that.arrivalDate) &&
                Objects.equals(departureDate, that.departureDate) &&
                Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arrivalDate, departureDate, address);
    }
}
