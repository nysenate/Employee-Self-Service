package gov.nysenate.ess.core.dao.pec.task.detail;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.pec.acknowledgment.AckDoc;
import org.springframework.stereotype.Repository;

/**
 * {@link PersonnelTaskDetailDao} for {@link AckDoc}s
 */
@Repository
public class AckDocTaskDetailDao extends SqlBaseDao implements PersonnelTaskDetailDao<AckDoc> {

    @Override
    public PersonnelTaskType taskType() {
        return PersonnelTaskType.DOCUMENT_ACKNOWLEDGMENT;
    }

    @Override
    public AckDoc getTaskDetails(PersonnelTask task) {
        return new AckDoc(task);
    }
}
