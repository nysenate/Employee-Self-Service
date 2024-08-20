package gov.nysenate.ess.travel.request.route;

import java.util.HashMap;
import java.util.Map;

public enum MethodOfTravel {
    PERSONAL_AUTO("Personal Auto", true),
    SENATE_VEHICLE("Senate Vehicle", false),
    CARPOOL("Carpool Rider", false),
    TRAIN("Train", false),
    AIRPLANE("Airplane", false),
    OTHER("Other", false);

    private final String displayName;
    private final boolean isMileageReimbursable;

    MethodOfTravel(String displayName, boolean isMileageReimbursable) {
        this.displayName = displayName;
        this.isMileageReimbursable = isMileageReimbursable;
    }

    /**
     * Attempts to map a name to a MethodOfTravel.
     * Both the enum name and display name are checked.
     */
    public static MethodOfTravel of(String name) {
        MethodOfTravel mot = map.get(name);
        if (mot == null) {
            mot = MethodOfTravel.valueOf(name);
        }
        return mot;
    }

    public boolean qualifiesForMileageReimbursement() {
        return isMileageReimbursable;
    }


    public String getDisplayName() {
        return displayName;
    }

    private static final Map<String, MethodOfTravel> map = new HashMap<>(values().length, 1);

    static {
        for (MethodOfTravel mot: values())
            map.put(mot.displayName, mot);
    }
}
