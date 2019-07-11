package gov.nysenate.ess.travel.authorization.role;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Represents the collection of roles a travel user can be assigned.
 * Most users will have none or a single role, but some users, specifically the SOS and MAJ
 * will have those roles in addition to the DEPT HD role.
 */
public class TravelRoles {

    // TODO Keep reference to the employee these roles are for?
    private ImmutableList<TravelRole> roles;
    private ImmutableList<TravelRole> delegatedRoles;

    public TravelRoles(List<TravelRole> roles, List<TravelRole> delegateRoles) {
        this.roles = ImmutableList.copyOf(roles);
        this.delegatedRoles = ImmutableList.copyOf(delegateRoles);
    }

    /**
     * Are any roles contained in this TravelRoles instance.
     */
    public boolean hasRole() {
        return !roles().isEmpty();
    }

    /**
     * All TravelRole's assigned to an employee
     * @return
     */
    public ImmutableList<TravelRole> roles() {
        return new ImmutableList.Builder<TravelRole>()
                .addAll(primaryRoles())
                .addAll(delegateRoles())
                .build();
    }

    /**
     * Roles explicitly assigned to this employee. i.e not delegated roles.
     * @return
     */
    public ImmutableList<TravelRole> primaryRoles() {
        return roles;
    }

    /**
     * The highest role this employee has.
     * Returns TravelRole.NONE if the employee does not have any travel roles.
     */
    public TravelRole apexRole() {
        TravelRole apex = TravelRole.NONE;
        if (roles().contains(TravelRole.SUPERVISOR)) {
            apex = TravelRole.SUPERVISOR;
        }
        if (roles().contains(TravelRole.DEPUTY_EXECUTIVE_ASSISTANT)) {
            apex = TravelRole.DEPUTY_EXECUTIVE_ASSISTANT;
        }
        if (roles().contains(TravelRole.SECRETARY_OF_THE_SENATE)) {
            apex = TravelRole.SECRETARY_OF_THE_SENATE;
        }
        if (roles().contains(TravelRole.MAJORITY_LEADER)) {
            apex = TravelRole.MAJORITY_LEADER;
        }
        return apex;
    }

    public ImmutableList<TravelRole> delegateRoles() {
        return delegatedRoles;
    }
}
