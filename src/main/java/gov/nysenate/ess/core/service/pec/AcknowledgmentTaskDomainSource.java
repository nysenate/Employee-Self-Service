package gov.nysenate.ess.core.service.pec;

import gov.nysenate.ess.core.dao.acknowledgment.AckDocDao;
import gov.nysenate.ess.core.model.acknowledgment.AckDoc;
import gov.nysenate.ess.core.model.pec.PersonnelTaskId;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provides all active tasks related to {@link AckDoc} acknowledgment.
 */
@Service
public class AcknowledgmentTaskDomainSource implements PersonnelTaskDomainSource {

    private final AckDocDao ackDocDao;

    public AcknowledgmentTaskDomainSource(AckDocDao ackDocDao) {
        this.ackDocDao = ackDocDao;
    }

    @Override
    public Set<PersonnelTaskId> getActiveTasks() {
        return ackDocDao.getActiveAckDocs().stream()
                .map(AckDoc::getTaskId)
                .collect(Collectors.toSet());
    }

}
