package gov.nysenate.ess.core.dao.pec.task.detail;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.pec.moodle.MoodleCourseTask;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
