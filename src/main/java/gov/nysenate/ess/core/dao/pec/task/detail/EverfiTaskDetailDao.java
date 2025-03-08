package gov.nysenate.ess.core.dao.pec.task.detail;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.pec.everfi.EverfiCourseTask;
import org.springframework.stereotype.Repository;

/**
 * {@link PersonnelTaskDetailDao} for {@link EverfiCourseTask}s
 */
@Repository
public class EverfiTaskDetailDao extends SqlBaseDao implements PersonnelTaskDetailDao<EverfiCourseTask> {

    @Override
    public PersonnelTaskType taskType() {
        return PersonnelTaskType.EVERFI_COURSE;
    }

    @Override
    public EverfiCourseTask getTaskDetails(PersonnelTask task) {
        return new EverfiCourseTask(task);
    }

}
