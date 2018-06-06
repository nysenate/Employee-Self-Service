package gov.nysenate.ess.travel.accommodation;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Accommodation {

    private final Address address;
    private final ImmutableSortedSet<Day> days;
    private final ImmutableSortedSet<Night> nights;

    public Accommodation(Address address, Set<Day> days, Set<Night> nights) {
        this.address = address;
        this.days = ImmutableSortedSet.copyOf(days);
        this.nights = ImmutableSortedSet.copyOf(nights);
    }

    /**
     * @return The total meal allowance for this accommodation.
     */
    public Dollars mealAllowance() {
        return getDays().stream()
                .map(Day::mealAllowance)
                .reduce(Dollars.ZERO, Dollars::add);
    }

    /**
     * @return The total lodging allowance for this accommodation.
     */
    public Dollars lodgingAllowance() {
        return getNights().stream()
                .map(Night::lodgingAllowance)
                .reduce(Dollars.ZERO, Dollars::add);
    }

    /**
     * @return The planned date of arrival.
     */
    public LocalDate arrivalDate() {
        return getDays().asList().get(0).getDate();
    }

    /**
     * @return The planned date of departure.
     */
    public LocalDate departureDate() {
        return getDays().asList().reverse().get(0).getDate();
    }

    public void setRequestMeals(boolean isRequestMeals, LocalDate date) {
        List<Day> days = getDays().stream().filter(d -> d.getDate().equals(date)).collect(Collectors.toList());
        if (days.size() > 0) {
            days.get(0).setMealsRequested(isRequestMeals);
        }
    }

    public void setRequestLodging(boolean isRequestLodging, LocalDate date) {
        List<Night> nights = getNights().stream().filter(n -> n.getDate().equals(date)).collect(Collectors.toList());
        if (nights.size() > 0) {
            nights.get(0).setLodgingRequested(isRequestLodging);
        }
    }

    protected Address getAddress() {
        return address;
    }

    protected ImmutableSet<Day> getDays() {
        return days;
    }

    protected ImmutableSet<Night> getNights() {
        return nights;
    }

    @Override
    public String toString() {
        return "Accommodation{" +
                "address=" + address +
                ", days=" + days +
                ", nights=" + nights +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Accommodation that = (Accommodation) o;
        return Objects.equals(address, that.address) &&
                Objects.equals(days, that.days) &&
                Objects.equals(nights, that.nights);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, days, nights);
    }
}