package gov.nysenate.ess.travel.application.route.destination;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.application.allowances.*;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingAllowances;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingPerDiem;
import gov.nysenate.ess.travel.application.allowances.meal.MealAllowances;
import gov.nysenate.ess.travel.application.allowances.meal.MealPerDiem;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Destination {

    private int id;
    private final Address address;
    private final Range<LocalDate> dateRange;
    private final TreeMap<LocalDate, PerDiem> mealPerDiems;
    private final TreeMap<LocalDate, PerDiem> lodgingPerDiems;

    public Destination(Address address, LocalDate arrival, LocalDate departure) {
        this(0, address, arrival, departure, new TreeMap<>(), new TreeMap<>());
    }

    public Destination(int id, Address address, LocalDate arrival, LocalDate departure,
                       Map<LocalDate, PerDiem> mealPerDiems,
                       Map<LocalDate, PerDiem> lodgingPerDiems) {
        this.id = id;
        this.address = address;
        this.dateRange = arrival != null && departure != null ? Range.closed(arrival, departure) : null;
        this.mealPerDiems = new TreeMap<>(mealPerDiems);
        this.lodgingPerDiems = new TreeMap<>(lodgingPerDiems);
    }

    public MealAllowances mealAllowances() {
        return new MealAllowances(getMealPerDiems().entrySet().stream()
                .map(entry -> new MealPerDiem(getAddress(), entry.getValue()))
                .collect(Collectors.toList()));
    }

    public LodgingAllowances lodgingAllowances() {
        return new LodgingAllowances(getLodgingPerDiems().entrySet().stream()
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
    public boolean wasVisitedOn(LocalDate date) {
        return getDateRange().contains(date);
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

    public void addMealPerDiem(LocalDate date, Dollars dollars) {
        getMealPerDiems().put(date, new PerDiem(date, dollars));
    }

    public void addLodgingPerDiem(LocalDate date, Dollars dollars) {
        getLodgingPerDiems().put(date, new PerDiem(date, dollars));
    }

    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    LocalDate arrivalDate() {
        return getDateRange().lowerEndpoint();
    }

    LocalDate departureDate() {
        return getDateRange().upperEndpoint();
    }

    Range<LocalDate> getDateRange() {
        return dateRange;
    }

    TreeMap<LocalDate, PerDiem> getMealPerDiems() {
        return mealPerDiems;
    }

    TreeMap<LocalDate, PerDiem> getLodgingPerDiems() {
        return lodgingPerDiems;
    }

    @Override
    public String toString() {
        return "Destination{" +
                "id=" + id +
                ", address=" + address +
                ", dateRange=" + dateRange +
                ", mealPerDiems=" + mealPerDiems +
                ", lodgingPerDiems=" + lodgingPerDiems +
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
                Objects.equals(mealPerDiems, that.mealPerDiems) &&
                Objects.equals(lodgingPerDiems, that.lodgingPerDiems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, address, dateRange, mealPerDiems, lodgingPerDiems);
    }
}