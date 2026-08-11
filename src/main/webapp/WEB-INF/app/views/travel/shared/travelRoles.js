/**
 * Display names for travel roles, mirroring TravelRole.java.
 *
 * The review APIs serialize pendingReviewerRole as the raw enum name, so the
 * UI needs its own mapping to present it.
 */
const ROLE_DISPLAY_NAMES = {
  DELEGATE: "Delegate",
  DEPARTMENT_HEAD: "Department Head",
  TRAVEL_ADMIN: "Travel Admin",
  SECRETARY_OF_THE_SENATE: "Secretary of the Senate",
  MAJORITY_LEADER: "Majority Leader",
};

/**
 * The display name for a travel role enum name, or null when there is no
 * meaningful role. TravelRole.NONE stands in for null on the server.
 */
export function travelRoleDisplayName(roleName) {
  if (!roleName || roleName === "NONE") {
    return null;
  }
  return ROLE_DISPLAY_NAMES[roleName] ?? roleName;
}
