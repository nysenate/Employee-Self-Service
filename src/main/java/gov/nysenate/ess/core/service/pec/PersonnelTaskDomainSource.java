package gov.nysenate.ess.core.service.pec;

import gov.nysenate.ess.core.model.pec.PersonnelTaskId;

import java.util.Set;

/**
 * A service that can provide a set of active PersonnelTasks required for all employees within a specific domain.
 *
 * The returned tasks do not necessarily include ALL tasks, though all {@link PersonnelTaskDomainSource} implementations
 * should collectively be able to do this.
 */
public interface PersonnelTaskDomainSource {

    /**
     * Returns a set of {@link PersonnelTaskId} assigned within the scope of this {@link PersonnelTaskDomainSource}.
     *
     * @return {@link Set<PersonnelTaskId>}
     */
    Set<PersonnelTaskId> getActiveTasks();
}
