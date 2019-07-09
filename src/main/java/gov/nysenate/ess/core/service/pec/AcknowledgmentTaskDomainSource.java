package gov.nysenate.ess.core.service.pec;

import gov.nysenate.ess.core.dao.acknowledgment.AckDocDao;
import gov.nysenate.ess.core.model.pec.PersonnelTaskId;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.pec.acknowledgment.AckDoc;
import gov.nysenate.ess.core.model.pec.acknowledgment.AckDocNotFoundEx;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.model.pec.PersonnelTaskType.DOCUMENT_ACKNOWLEDGMENT;

/**
 * Provides all active tasks related to {@link AckDoc} acknowledgment.
 */
@Service
public class AcknowledgmentTaskDomainSource implements PersonnelTaskDomainSource<AckDoc> {

    private final AckDocDao ackDocDao;

    public AcknowledgmentTaskDomainSource(AckDocDao ackDocDao) {
        this.ackDocDao = ackDocDao;
    }

    @Override
    public Set<PersonnelTaskId> getTaskIds(boolean activeOnly) {
        return ackDocDao.getAckDocs(activeOnly).stream()
                .map(AckDoc::getTaskId)
                .collect(Collectors.toSet());
    }

    @Override
    public PersonnelTaskType getTaskType() {
        return DOCUMENT_ACKNOWLEDGMENT;
    }

    @Override
    public AckDoc getPersonnelTask(int ackDocId) throws PersonnelTaskNotFoundEx {
        try {
            return ackDocDao.getAckDoc(ackDocId);
        } catch (AckDocNotFoundEx ex) {
            throw new PersonnelTaskNotFoundEx(new PersonnelTaskId(DOCUMENT_ACKNOWLEDGMENT, ackDocId));
        }
    }

    @Override
    public List<AckDoc> getTasks(boolean activeOnly) {
        return ackDocDao.getAckDocs(activeOnly);
    }
}
