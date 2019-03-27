package gov.nysenate.ess.travel;

import java.util.Optional;

public enum TravelRole {
    DEPARTMENT_HEAD,
    DEPUTY_EXECUTIVE_ASSISTANT,
    SECRETARY_OF_THE_SENATE,
    MAJORITY_LEADER;

    public Optional<TravelRole> next() {
        TravelRole[] roles = TravelRole.values();
        for (int i = 0; i < roles.length; i++) {
            if (roles[i] == this) {
                return Optional.ofNullable(roles[i + 1]);
            }
        }
        return Optional.empty();
    }
}
