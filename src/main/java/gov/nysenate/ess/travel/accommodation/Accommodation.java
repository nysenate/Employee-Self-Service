package gov.nysenate.ess.travel.accommodation;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.Dollars;

import java.time.LocalDate;


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

    public Dollars mealAllowance() {
        if (!isMealsRequested()) {
            return Dollars.ZERO;
        }
        return getStays().stream()
                .map(Stay::mealAllowance)
                .reduce(Dollars.ZERO, Dollars::add);
    }

    public Dollars lodgingAllowance() {
        if (!isLodgingRequested()) {
            return Dollars.ZERO;
        }
        return getStays().stream()
                .map(Stay::lodgingAllowance)
                .reduce(Dollars.ZERO, Dollars::add);
    }

    public LocalDate arrivalDate() {
        return getStays().asList().get(0).getDate();
    }

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
}