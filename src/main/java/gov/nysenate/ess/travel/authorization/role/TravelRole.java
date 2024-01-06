package gov.nysenate.ess.travel.authorization.role;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum TravelRole {

    /** Role edge cases */

    // The DELEGATE role is assigned to users who are a delegate.
    // This is used to disallow them from modifying delegates themselves.
    DELEGATE("Delegate"),
    // NONE is used in place of null.
    NONE("None"),

    /** Travel Roles */
    SUPERVISOR("Supervisor"),
    DEPUTY_EXECUTIVE_ASSISTANT("Deputy Executive Assistant"),
    SECRETARY_OF_THE_SENATE("Secretary of the Senate"),
    MAJORITY_LEADER("Majority Leader");

    /**
     * Map of display names to TravelRole's for construction from display names.
     */
    private static final Map<String, TravelRole> displayNameToRole = Arrays.stream(values())
            .collect(Collectors.toMap(role -> role.displayName, Function.identity()));

    private final String displayName;

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

    public String getDisplayName() {
        return displayName;
    }
}
