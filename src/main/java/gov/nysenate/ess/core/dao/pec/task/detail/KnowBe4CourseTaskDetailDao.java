package gov.nysenate.ess.core.dao.pec.task.detail;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.pec.knowbe4.KnowBe4CourseTask;
import org.springframework.stereotype.Repository;

@Repository
public class KnowBe4CourseTaskDetailDao extends SqlBaseDao implements PersonnelTaskDetailDao<KnowBe4CourseTask> {
    @Override
    public PersonnelTaskType taskType() {
        return PersonnelTaskType.KNOWBE4_COURSE;
    }

    @Override
    public KnowBe4CourseTask getTaskDetails(PersonnelTask task) {
        return new KnowBe4CourseTask(task);
    }
}
