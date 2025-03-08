package gov.nysenate.ess.core.dao.pec.task.detail;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.pec.ethics.EthicsCourseTask;
import org.springframework.stereotype.Repository;

/**
 * {@link PersonnelTaskDetailDao} for {@link gov.nysenate.ess.core.model.pec.ethics.EthicsCourseTask}s
 */
@Repository
public class EthicsCourseTaskDetailDao extends SqlBaseDao implements PersonnelTaskDetailDao<EthicsCourseTask> {

    @Override
    public PersonnelTaskType taskType() {
        return PersonnelTaskType.ETHICS_COURSE;
    }

    @Override
    public EthicsCourseTask getTaskDetails(PersonnelTask task) {
        return new EthicsCourseTask(task);
    }
}
