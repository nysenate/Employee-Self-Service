package gov.nysenate.ess.travel.application.destination;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.unit.Address;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Destination {

    private final Address address;
    private final Range<LocalDate> dateRange;

    public Destination(Address address, LocalDate arrivalDate, LocalDate departureDate) {
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

    public Address getAddress() {
        return address;
    }

    protected Range<LocalDate> getDateRange() {
        return this.dateRange;
    }
}