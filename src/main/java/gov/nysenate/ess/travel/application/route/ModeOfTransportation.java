package gov.nysenate.ess.travel.application.route;


import java.util.Objects;

/**
 * Describes the method of travel for the leg of a travel application.
 * Mostly wraps {@link MethodOfTravel}, however it adds a customizable
 * description field which is entered by the user if their MethodOfTravel is OTHER.
 */
public class ModeOfTransportation {

    private final MethodOfTravel methodOfTravel;
    private final String description;

    /**
     * Static instances, Use one of these unless MethodOfTravel is OTHER.
     */
    public static final ModeOfTransportation PERSONAL_AUTO =
            new ModeOfTransportation(MethodOfTravel.PERSONAL_AUTO, MethodOfTravel.PERSONAL_AUTO.getDisplayName());

    public static final ModeOfTransportation SENATE_VEHICLE =
            new ModeOfTransportation(MethodOfTravel.SENATE_VEHICLE, MethodOfTravel.SENATE_VEHICLE.getDisplayName());

    public static final ModeOfTransportation CARPOOL =
            new ModeOfTransportation(MethodOfTravel.CARPOOL, MethodOfTravel.CARPOOL.getDisplayName());

    public static final ModeOfTransportation TRAIN =
            new ModeOfTransportation(MethodOfTravel.TRAIN, MethodOfTravel.TRAIN.getDisplayName());

    public static final ModeOfTransportation AIRPLANE =
            new ModeOfTransportation(MethodOfTravel.AIRPLANE, MethodOfTravel.AIRPLANE.getDisplayName());

    /**
     * Should use static instances above whenever possible.
     * This constructor is useful when MethodOfTravel = OTHER
     * and when instanciating from a view.
     * @param methodOfTravel
     * @param description
     */
    // TODO make this private? should always be using other constructor with more logic unless initializing statics.
    public ModeOfTransportation(MethodOfTravel methodOfTravel, String description) {
        this.methodOfTravel = methodOfTravel;
        this.description = description;
    }

    /**
     * @param methodOfTravelString String representing the MethodOfTravel Enum or display name.
     * @param description
     */
    public ModeOfTransportation(String methodOfTravelString, String description) {
        this.methodOfTravel = MethodOfTravel.of(methodOfTravelString);
        // Only use description if OTHER, otherwise default to the MethodOfTravel's display name.
        if (MethodOfTravel.of(methodOfTravelString) == MethodOfTravel.OTHER) {
            this.description = description;
        }
        else {
            this.description = MethodOfTravel.of(methodOfTravelString).getDisplayName();
        }
    }

    public boolean qualifiesForMileageReimbursement() {
        return getMethodOfTravel().qualifiesForMileageReimbursement();
    }

    public MethodOfTravel getMethodOfTravel() {
        return methodOfTravel;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "ModeOfTransportation{" +
                "methodOfTravel=" + methodOfTravel +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModeOfTransportation that = (ModeOfTransportation) o;
        return methodOfTravel == that.methodOfTravel &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodOfTravel, description);
    }
}
