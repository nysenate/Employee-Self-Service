package gov.nysenate.ess.travel.authorization.role;

import java.util.HashMap;
import java.util.Map;

/**
 * Roles involved in the approving of Travel Applications.
 */
public enum TravelRole {

    /** Role edge cases */

    // The DELEGATE role is assigned to users who are a delegate.
    // This is used to disallow them from modifying delegates themselves.
    DELEGATE("Delegate", false),
    // NONE is used in place of null.
    NONE("None", false),

    /** Travel Roles */
    DEPARTMENT_HEAD("Department Head", false),
    TRAVEL_ADMIN("Travel Admin", true),
    SECRETARY_OF_THE_SENATE("Secretary of the Senate", true),
    MAJORITY_LEADER("Majority Leader", false);

    protected String displayName;
    // Can this role view apps that have be shared by reviewers for collaboration purposes.
    protected boolean canViewShared;

    TravelRole(String displayName, boolean canViewShared) {
        this.displayName = displayName;
        this.canViewShared = canViewShared;
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
