package gov.nysenate.ess.travel.application.route.destination;

import com.google.common.collect.Range;
import gov.nysenate.ess.travel.application.address.TravelAddress;
import gov.nysenate.ess.travel.application.allowances.PerDiem;

import java.time.LocalDate;
import java.util.*;

public class Destination {

    protected int id;
    protected final TravelAddress address;
    protected final Range<LocalDate> dateRange;
    protected Set<PerDiem> mealPerDiems;
    protected Set<PerDiem> lodgingPerDiems;

    public Destination(TravelAddress address, LocalDate arrival, LocalDate departure) {
        this(0, address, arrival, departure, new TreeMap<>(), new TreeMap<>());
    }

    public Destination(int id, TravelAddress address, LocalDate arrival, LocalDate departure,
                       Map<LocalDate, PerDiem> dateToMealPerDiems,
                       Map<LocalDate, PerDiem> dateToLodgingPerDiems) {
        this.id = id;
        this.address = address;
        this.dateRange = arrival != null && departure != null ? Range.closed(arrival, departure) : null;
        this.mealPerDiems = new HashSet<>(dateToMealPerDiems.values());
        this.lodgingPerDiems = new HashSet<>(dateToLodgingPerDiems.values());
    }

    public Destination(int id, TravelAddress address, LocalDate arrival, LocalDate departure,
                       Collection<PerDiem> mealPerDiems, Collection<PerDiem> lodgingPerDiems) {
        this.id = id;
        this.address = address;
        this.dateRange = arrival != null && departure != null ? Range.closed(arrival, departure) : null;
        this.mealPerDiems = mealPerDiems == null ? new HashSet<>() : new HashSet<>(mealPerDiems);
        this.lodgingPerDiems = lodgingPerDiems == null ? new HashSet<>() : new HashSet<>(lodgingPerDiems);
    }

    public Set<PerDiem> mealPerDiems() {
        return mealPerDiems;
    }

    public Set<PerDiem> lodgingPerDiems() {
        return lodgingPerDiems;
    }

    public TravelAddress getAddress() {
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
     * Days at this destination.
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
        for (LocalDate date = arrivalDate().plusDays(1); date.isBefore(departureDate().plusDays(1)); date = date.plusDays(1)) {
            nights.add(date);
        }

        return nights;
    }

    public void addMealPerDiem(PerDiem perDiem) {
        mealPerDiems.add(perDiem);
    }

    public void addLodgingPerDiem(PerDiem perDiem) {
        lodgingPerDiems.add(perDiem);
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
