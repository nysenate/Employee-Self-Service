package gov.nysenate.ess.travel.accommodation;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.util.Objects;

public class Accommodation {

    private final Address address;
    private final ImmutableSet<Stay> stays;
    private final boolean isMealsRequested;
    private final boolean isLodgingRequested;

    public Accommodation(Address address, ImmutableSet<Stay> stays) {
        this(address, stays, true, true);
    }

    public Accommodation(Address address, ImmutableSet<Stay> stays, boolean isMealsRequested, boolean isLodgingRequested) {
        this.address = address;
        this.stays = stays;
        this.isMealsRequested = isMealsRequested;
        this.isLodgingRequested = isLodgingRequested;
    }

    /**
     * @return The total meal allowance for this accommodation.
     */
    public Dollars mealAllowance() {
        if (!isMealsRequested()) {
            return Dollars.ZERO;
        }
        return getStays().stream()
                .map(Stay::mealAllowance)
                .reduce(Dollars.ZERO, Dollars::add);
    }

    /**
     * @return The total lodging allowance for this accommodation.
     */
    public Dollars lodgingAllowance() {
        if (!isLodgingRequested()) {
            return Dollars.ZERO;
        }
        return getStays().stream()
                .map(Stay::lodgingAllowance)
                .reduce(Dollars.ZERO, Dollars::add);
    }

    /**
     * @return The planned date of arrival.
     */
    public LocalDate arrivalDate() {
        return getStays().asList().get(0).getDate();
    }

    /**
     * @return The planned date of departure.
     */
    public LocalDate departureDate() {
        return getStays().asList().reverse().get(0).getDate();
    }

    protected Address getAddress() {
        return address;
    }

    protected ImmutableSet<Stay> getStays() {
        return stays;
    }

    protected boolean isMealsRequested() {
        return isMealsRequested;
    }

    protected boolean isLodgingRequested() {
        return isLodgingRequested;
    }

    @Override
    public String toString() {
        return "Accommodation{" +
                "address=" + address +
                ", stays=" + stays +
                ", isMealsRequested=" + isMealsRequested +
                ", isLodgingRequested=" + isLodgingRequested +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Accommodation that = (Accommodation) o;
        return isMealsRequested == that.isMealsRequested &&
                isLodgingRequested == that.isLodgingRequested &&
                Objects.equals(address, that.address) &&
                Objects.equals(stays, that.stays);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, stays, isMealsRequested, isLodgingRequested);
    }
}