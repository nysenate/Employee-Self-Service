package gov.nysenate.ess.travel.authorization.role;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Represents the collection of roles a travel user can be assigned.
 * Most users will have none or a single role, but some users, specifically the SOS and MAJ
 * will have those roles in addition to the DEPT HD role.
 */
public class TravelRoles {

    private ImmutableList<TravelRole> roles;
    private ImmutableList<TravelRole> delegatedRoles;

    public TravelRoles(List<TravelRole> roles, List<TravelRole> delegateRoles) {
        this.roles = ImmutableList.copyOf(roles);
        this.delegatedRoles = ImmutableList.copyOf(delegateRoles);
    }

    /**
     * All TravelRole's assigned to an employee
     * @return
     */
    public ImmutableList<TravelRole> all() {
        return new ImmutableList.Builder<TravelRole>()
                .addAll(primary())
                .addAll(delegate())
                .build();
    }

    /**
     * Roles explicitly assigned to this employee. i.e not delegated roles.
     * @return
     */
    public ImmutableList<TravelRole> primary() {
        return roles;
    }

    /**
     * The highest role this employee has.
     * Returns TravelRole.NONE if the employee does not have any travel roles.
     */
    public TravelRole apex() {
        TravelRole apex = TravelRole.NONE;
        if (all().contains(TravelRole.SUPERVISOR)) {
            apex = TravelRole.SUPERVISOR;
        }
        if (all().contains(TravelRole.TRAVEL_ADMIN)) {
            apex = TravelRole.TRAVEL_ADMIN;
        }
        if (all().contains(TravelRole.SECRETARY_OF_THE_SENATE)) {
            apex = TravelRole.SECRETARY_OF_THE_SENATE;
        }
        if (all().contains(TravelRole.MAJORITY_LEADER)) {
            apex = TravelRole.MAJORITY_LEADER;
        }
        return apex;
    }

    public ImmutableList<TravelRole> delegate() {
        return delegatedRoles;
    }
}
