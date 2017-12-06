package gov.nysenate.ess.travel.application.model;

import java.util.HashMap;
import java.util.Map;

public enum ModeOfTransportation {
    PERSONAL_AUTO("Personal Auto"),
    SENATE_VEHICLE("Senate Vehicle"),
    TRAIN("Train"),
    AIRPLANE("Airplane"),
    OTHER("Other");

    private final String displayName;

    ModeOfTransportation(String displayName) {
        this.displayName = displayName;
    }

    public static ModeOfTransportation of(String displayName) {
        ModeOfTransportation mot = map.get(displayName);
        if (mot == null) {
            throw new IllegalArgumentException("Invalid display name: " + displayName);
        }
        return mot;
    }

    private static final Map<String, ModeOfTransportation> map = new HashMap<>(values().length, 1);

    static {
        for (ModeOfTransportation mot: values())
            map.put(mot.displayName, mot);
    }
}
