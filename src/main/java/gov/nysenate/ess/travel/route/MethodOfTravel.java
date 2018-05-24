package gov.nysenate.ess.travel.route;

import java.util.HashMap;
import java.util.Map;

public enum MethodOfTravel {
    PERSONAL_AUTO("Personal Auto", true),
    SENATE_VEHICLE("Senate Vehicle", false),
    CARPOOL("Carpool", false),
    TRAIN("Train", false),
    AIRPLANE("Airplane", false),
    OTHER("Other", false);

    private final String displayName;
    private final boolean isMileageReimbursable;

    MethodOfTravel(String displayName, boolean isMileageReimbursable) {
        this.displayName = displayName;
        this.isMileageReimbursable = isMileageReimbursable;
    }

    public static MethodOfTravel of(String displayName) {
        MethodOfTravel mot = map.get(displayName);
        if (mot == null) {
            throw new IllegalArgumentException("Invalid display name: " + displayName);
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
