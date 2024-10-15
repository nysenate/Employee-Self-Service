package gov.nysenate.ess.travel.request.route.destination;

import com.google.common.collect.Range;
import gov.nysenate.ess.travel.request.address.TravelAddress;

import java.time.LocalDate;
import java.util.*;

public class Destination {

    protected int id;
    protected final TravelAddress address;
    protected final Range<LocalDate> dateRange;

    public Destination(int id) {
        this(id, null, null, null);
    }

    public Destination(TravelAddress address, LocalDate arrival, LocalDate departure) {
        this(0, address, arrival, departure);
    }

    public Destination(int id, TravelAddress address, LocalDate arrival, LocalDate departure) {
        this.id = id;
        this.address = address;
        this.dateRange = arrival != null && departure != null ? Range.closed(arrival, departure) : null;
    }

    /**
     * Days at this destination.
     *
     * @return
     */
    public Set<LocalDate> days() {
        Set<LocalDate> days = new HashSet<>();
        LocalDate date = arrivalDate();
        while (date.isBefore(departureDate().plusDays(1))) {
            days.add(date);
            date = date.plusDays(1);
        }
        return days;
    }

    /**
     * @return A list of dates, each representing an overnight stay at this destination.
     */
    public List<LocalDate> nights() {
        if (arrivalDate().equals(departureDate())) {
            return new ArrayList<>();
        }

        List<LocalDate> nights = new ArrayList<>();
        for (LocalDate date = arrivalDate(); date.isBefore(departureDate()); date = date.plusDays(1)) {
            nights.add(date);
        }

        return nights;
    }

    public TravelAddress getAddress() {
        return address;
    }


    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    LocalDate arrivalDate() {
        if (dateRange == null) {
            return null;
        }
        return dateRange.lowerEndpoint();
    }

    LocalDate departureDate() {
        if (dateRange == null) {
            return null;
        }
        return dateRange.upperEndpoint();
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
        return id == that.id && Objects.equals(address, that.address) && Objects.equals(dateRange, that.dateRange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, address, dateRange);
    }
}
