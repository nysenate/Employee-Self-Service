package gov.nysenate.ess.web.model.personnel;

import java.util.HashSet;
import java.util.Set;

/**
 * Stores any modifications that are made to the supervisor chain for an employee.
 */
public class SupervisorChainAlteration
{
    protected Set<Integer> chainInclusions;
    protected Set<Integer> chainExclusions;

    /** --- Constructors --- */

    public SupervisorChainAlteration(Set<Integer> chainInclusions, Set<Integer> chainExclusions) {
        this.chainInclusions = chainInclusions;
        this.chainExclusions = chainExclusions;
    }

    /** --- Functional Getters --- */

    public boolean hasAnyAlterations() {
        return (chainInclusions != null && !chainInclusions.isEmpty()) ||
                (chainExclusions != null && !chainExclusions.isEmpty());
    }

    /** --- Basic Getters --- */

    public Set<Integer> getChainInclusions() {
        return chainInclusions;
    }

    public Set<Integer> getChainExclusions() {
        return chainExclusions;
    }
}