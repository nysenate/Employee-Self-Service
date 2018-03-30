package gov.nysenate.ess.travel.route;

import java.util.HashMap;
import java.util.Map;

public enum ModeOfTransportation {
    PERSONAL_AUTO("Personal Auto", true),
    SENATE_VEHICLE("Senate Vehicle", false),
    TRAIN("Train", false),
    AIRPLANE("Airplane", false),
    OTHER("Other", false);

    private final String displayName;
    private final boolean isMileageReimbursable;

    ModeOfTransportation(String displayName, boolean isMileageReimbursable) {
        this.displayName = displayName;
        this.isMileageReimbursable = isMileageReimbursable;
    }

    public static ModeOfTransportation of(String displayName) {
        ModeOfTransportation mot = map.get(displayName);
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

    private static final Map<String, ModeOfTransportation> map = new HashMap<>(values().length, 1);

    static {
        for (ModeOfTransportation mot: values())
            map.put(mot.displayName, mot);
    }
}
