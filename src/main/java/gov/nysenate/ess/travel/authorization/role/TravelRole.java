package gov.nysenate.ess.travel.authorization.role;

import java.util.HashMap;
import java.util.Map;

public enum TravelRole {

    /** Role edge cases */

    // The DELEGATE role is assigned to users who are a delegate.
    // This is used to disallow them from modifying delegates themselves.
    DELEGATE("Delegate"),
    // NONE is used in place of null.
    NONE("None"),

    /** Travel Roles */
    SUPERVISOR("Supervisor"),
    TRAVEL_ADMIN("Travel Admin"),
    SECRETARY_OF_THE_SENATE("Secretary of the Senate"),
    MAJORITY_LEADER("Majority Leader");

    protected String displayName;

    TravelRole(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Constructs a TravelRole from either the display name or enum name.
     * @param name
     * @return
     */
    public static TravelRole of(String name) {
        TravelRole role = displayNameToRole.get(name);
        if (role == null) {
            role = TravelRole.valueOf(name);
        }
        return role;
    }

    /**
     * Map of display names to TravelRole's for construction from display names.
     */
    private static final Map<String, TravelRole> displayNameToRole = new HashMap<>(values().length, 1);

    static {
        for (TravelRole r: values()) {
            displayNameToRole.put(r.displayName, r);
        }
    }
}
