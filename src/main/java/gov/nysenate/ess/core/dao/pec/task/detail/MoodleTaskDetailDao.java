package gov.nysenate.ess.core.dao.pec.task.detail;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.pec.moodle.MoodleCourseTask;
import org.springframework.stereotype.Repository;

/**
 * {@link PersonnelTaskDetailDao} for {@link MoodleCourseTask}s
 */
@Repository
public class MoodleTaskDetailDao extends SqlBaseDao implements PersonnelTaskDetailDao<MoodleCourseTask> {

    @Override
    public PersonnelTaskType taskType() {
        return PersonnelTaskType.MOODLE_COURSE;
    }

    @Override
    public MoodleCourseTask getTaskDetails(PersonnelTask task) {
        return new MoodleCourseTask(task);
    }
}
