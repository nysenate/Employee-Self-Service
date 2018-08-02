package gov.nysenate.ess.travel.application.destination;

import com.google.common.collect.ImmutableList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Destinations {

    private final ImmutableList<Destination> destinations;

    public Destinations(List<Destination> destinations) {
        this.destinations = ImmutableList.copyOf(destinations);
    }

    public ImmutableList<Destination> destinationsForDate(LocalDate date) {
        List<Destination> destinations = new ArrayList<>();
        for (Destination dest : getDestinations()) {
            if (dest.getDateRange().contains(date)) {
                destinations.add(dest);
            }
        }
        return ImmutableList.copyOf(destinations);
    }

    public LocalDate startDate() {
        LocalDate startDate = LocalDate.MAX;
        for (Destination dest : getDestinations()) {
            if (dest.arrivalDate().isBefore(startDate)) {
                startDate = dest.arrivalDate();
            }
        }
        return startDate;
    }

    public LocalDate endDate() {
        LocalDate endDate = LocalDate.MIN;
        for (Destination dest : getDestinations()) {
            if (dest.departureDate().isAfter(endDate)) {
                endDate = dest.departureDate();
            }
        }
        return endDate;
    }

    public int size() {
        return getDestinations().size();
    }

    public ImmutableList<Destination> getDestinations() {
        return this.destinations;
    }

    @Override
    public String toString() {
        return "Destinations{" +
                "destinations=" + destinations +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Destinations that = (Destinations) o;
        return Objects.equals(destinations, that.destinations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(destinations);
    }
}
