package gov.nysenate.ess.travel.application.destination;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import gov.nysenate.ess.travel.application.address.TravelAddress;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Destination {

    private final UUID id;
    private final TravelAddress address;
    private final Range<LocalDate> dateRange;

    public Destination(UUID id, TravelAddress address, LocalDate arrivalDate, LocalDate departureDate) {
        this.id = id;
        this.address = address;
        this.dateRange = Range.closed(arrivalDate, departureDate);
    }

    /**
     * @return The planned date of arrival.
     */
    public LocalDate arrivalDate() {
        return getDateRange().lowerEndpoint();
    }

    /**
     * @return The planned date of departure.
     */
    public LocalDate departureDate() {
        return getDateRange().upperEndpoint();
    }

    /**
     * @return A list of nights spent at this destination.
     * These dates are eligible for lodging allowances.
     */
    public ImmutableList<LocalDate> nights() {
        LocalDate arrival = arrivalDate();
        LocalDate departure = departureDate();

        if (arrival.equals(departure)) {
            return ImmutableList.of();
        }

        List<LocalDate> nights = new ArrayList<>();
        for (LocalDate date = arrival.plusDays(1); date.isBefore(departure.plusDays(1)); date = date.plusDays(1)) {
            nights.add(date);
        }

        return ImmutableList.copyOf(nights);
    }

    public TravelAddress getAddress() {
        return address;
    }

    protected UUID getId() {
        return id;
    }

    protected Range<LocalDate> getDateRange() {
        return this.dateRange;
    }

    @Override
    public String toString() {
        return "Destination{" +
                "id=" + id +
                ", address=" + address +
                ", dateRange=" + dateRange +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Destination that = (Destination) o;
        return Objects.equals(address, that.address) &&
                Objects.equals(dateRange, that.dateRange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, dateRange);
    }
}