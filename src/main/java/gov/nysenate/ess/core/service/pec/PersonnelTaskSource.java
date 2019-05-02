package gov.nysenate.ess.core.service.pec;

import gov.nysenate.ess.core.model.pec.PersonnelTaskId;

import java.util.Set;

/**
 * Can provide a set of all active personnel tasks.
 */
public interface PersonnelTaskSource {

    /**
     * Gets a set of all currently active {@link PersonnelTaskId}s
     * @return {@link Set<PersonnelTaskId>}
     */
    Set<PersonnelTaskId> getAllPersonnelTaskIds();
}
