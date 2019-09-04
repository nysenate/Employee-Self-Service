package gov.nysenate.ess.travel.application.route.destination;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.application.allowances.PerDiem;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingPerDiem;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingPerDiems;
import gov.nysenate.ess.travel.application.allowances.meal.MealPerDiem;
import gov.nysenate.ess.travel.application.allowances.meal.MealPerDiems;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Destination {

    protected int id;
    protected final Address address;
    protected final Range<LocalDate> dateRange;
    // TODO ImmutableSortedMap?
    protected final TreeMap<LocalDate, PerDiem> dateToMealPerDiems;
    protected final TreeMap<LocalDate, PerDiem> dateToLodgingPerDiems;

    public Destination(Address address, LocalDate arrival, LocalDate departure) {
        this(0, address, arrival, departure, new TreeMap<>(), new TreeMap<>());
    }

    public Destination(int id, Address address, LocalDate arrival, LocalDate departure,
                       Map<LocalDate, PerDiem> dateToMealPerDiems,
                       Map<LocalDate, PerDiem> dateToLodgingPerDiems) {
        // TODO validate PerDiem addresses match destination address?
        this.id = id;
        this.address = address;
        this.dateRange = arrival != null && departure != null ? Range.closed(arrival, departure) : null;
        this.dateToMealPerDiems = dateToMealPerDiems == null ? new TreeMap<>() : new TreeMap<>(dateToMealPerDiems);
        this.dateToLodgingPerDiems = dateToLodgingPerDiems == null ? new TreeMap<>() : new TreeMap<>(dateToLodgingPerDiems);
    }

    public MealPerDiems mealPerDiems() {
        return new MealPerDiems(dateToMealPerDiems.entrySet().stream()
                .map(entry -> new MealPerDiem(getAddress(), entry.getValue()))
                .collect(Collectors.toList()));
    }

    public LodgingPerDiems lodgingPerDiems() {
        return new LodgingPerDiems(dateToLodgingPerDiems.entrySet().stream()
                .map(entry -> new LodgingPerDiem(getAddress(), entry.getValue()))
                .collect(Collectors.toList()));
    }

    public Address getAddress() {
        return address;
    }

    /**
     * @param date
     * @return true if this destination will be visited on {@code date}. Otherwise false.
     */
    // TODO instead of this add a method Route.destinationsOn(date)
    public boolean wasVisitedOn(LocalDate date) {
        return dateRange.contains(date);
    }

    /**
     * @return A list of dates, each representing an overnight stay at this destination.
     */
    public List<LocalDate> nights() {
        if (arrivalDate().equals(departureDate())) {
            return new ArrayList<>();
        }

        List<LocalDate> nights = new ArrayList<>();
        for (LocalDate date = arrivalDate().plusDays(1); date.isBefore(departureDate().plusDays(1)); date = date.plusDays(1)) {
            nights.add(date);
        }

        return nights;
    }

    public void addMealPerDiem(PerDiem perDiem) {
        dateToMealPerDiems.put(perDiem.getDate(), perDiem);
    }

    public void addLodgingPerDiem(PerDiem perDiem) {
        dateToLodgingPerDiems.put(perDiem.getDate(), perDiem);
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
                ", mealPerDiems=" + dateToMealPerDiems +
                ", lodgingPerDiems=" + dateToLodgingPerDiems +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Destination that = (Destination) o;
        return id == that.id &&
                Objects.equals(address, that.address) &&
                Objects.equals(dateRange, that.dateRange) &&
                Objects.equals(dateToMealPerDiems, that.dateToMealPerDiems) &&
                Objects.equals(dateToLodgingPerDiems, that.dateToLodgingPerDiems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, address, dateRange, dateToMealPerDiems, dateToLodgingPerDiems);
    }
}