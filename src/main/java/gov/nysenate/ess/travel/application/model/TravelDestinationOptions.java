package gov.nysenate.ess.travel.application.model;


import java.util.Objects;

public class TravelDestinationOptions {

    private final ModeOfTransportation modeOfTransportation;
    private final boolean requestMeals;
    private final boolean requestLodging;
    private final boolean requestMileage;

    public TravelDestinationOptions(ModeOfTransportation modeOfTransportation) {
        this.modeOfTransportation = modeOfTransportation;
        this.requestMeals = false;
        this.requestLodging = false;
        this.requestMileage = false;
    }

    public TravelDestinationOptions(ModeOfTransportation modeOfTransportation, boolean requestMeals,
                                    boolean requestLodging, boolean requestMileage) {
        this.modeOfTransportation = modeOfTransportation;
        this.requestMeals = requestMeals;
        this.requestLodging = requestLodging;
        this.requestMileage = requestMileage;
    }

    public TravelDestinationOptions defaultAllowances() {
        return new TravelDestinationOptions(getModeOfTransportation(), true, true, true);
    }

    public TravelDestinationOptions requestMeals() {
        return new TravelDestinationOptions(getModeOfTransportation(), true,
                isRequestLodging(), isRequestMileage());
    }

    public TravelDestinationOptions requestLodging() {
         return new TravelDestinationOptions(getModeOfTransportation(), isRequestMeals(),
                true, isRequestMileage());
    }

    public TravelDestinationOptions requestMileage() {
         return new TravelDestinationOptions(getModeOfTransportation(), isRequestMeals(),
                isRequestLodging(), true);
    }

    public boolean isMileageReimbursable() {
        return getModeOfTransportation() == ModeOfTransportation.PERSONAL_AUTO
                && isRequestMileage();
    }

    public ModeOfTransportation getModeOfTransportation() {
        return modeOfTransportation;
    }

    public boolean isRequestMeals() {
        return requestMeals;
    }

    public boolean isRequestLodging() {
        return requestLodging;
    }

    public boolean isRequestMileage() {
        return requestMileage;
    }

    @Override
    public String toString() {
        return "TravelDestinationOptions{" +
                "modeOfTransportation=" + modeOfTransportation +
                ", requestMeals=" + requestMeals +
                ", requestLodging=" + requestLodging +
                ", requestMileage=" + requestMileage +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TravelDestinationOptions that = (TravelDestinationOptions) o;
        return requestMeals == that.requestMeals &&
                requestLodging == that.requestLodging &&
                requestMileage == that.requestMileage &&
                modeOfTransportation == that.modeOfTransportation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(modeOfTransportation, requestMeals, requestLodging, requestMileage);
    }
}
