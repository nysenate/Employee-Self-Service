package gov.nysenate.ess.core.service.pec;

import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.cache.CacheType;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskTestBuilder;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.service.cache.EssCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;

import java.util.Set;

import static gov.nysenate.ess.core.model.pec.PersonnelTaskType.*;

/**
 * Utility class that allows alteration of personnel task tables for testing purposes
 */
@Service
public class PersonnelTaskTestDao extends SqlBaseDao {

    private static final Logger logger = LoggerFactory.getLogger(PersonnelTaskTestDao.class);

    private static final String taskInsertSql = "" +
            "INSERT INTO ess.personnel_task(\n" +
            "  task_type, title, effective_date_time, end_date_time, active, assignment_group, url, resource)\n" +
            "VALUES(:taskType::ess.personnel_task_type, :title, :effectiveDateTime, :endDateTime, :active,\n" +
            "  :assignmentGroup::ess.personnel_task_assignment_group, :url, :resource)\n"
            ;

    private static final String taskIdCol = "task_id";

    private static final String setActiveSql = "" +
            "UPDATE ess.personnel_task\n" +
            "SET active = :active\n" +
            "WHERE task_id = :taskId";

    private final EventBus eventBus;

    public PersonnelTaskTestDao(EventBus eventBus) {
        logger.warn("Initializing personnel task test dao");
        this.eventBus = eventBus;
    }

    /**
     * Inserts a dummy task returning a copy of the task including the generated taskId.
     *
     * @param taskBuilder {@link PersonnelTaskTestBuilder}
     * @return {@link PersonnelTask}
     */
    public PersonnelTask insertDummyTask(PersonnelTaskTestBuilder taskBuilder) {
        PersonnelTask dummyTask = taskBuilder.build();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("taskType", dummyTask.getTaskType().name())
                .addValue("title", dummyTask.getTitle())
                .addValue("effectiveDateTime", toDate(dummyTask.getEffectiveDateTime()))
                .addValue("endDateTime", toDate(dummyTask.getEndDateTime()))
                .addValue("active", dummyTask.isActive())
                .addValue("assignmentGroup", dummyTask.getAssignmentGroup().name())
                .addValue("url", dummyTask.getUrl())
                .addValue("resource", dummyTask.getResource());
        GeneratedKeyHolder taskIdKeyHolder = new GeneratedKeyHolder();

        localNamedJdbc.update(taskInsertSql, params, taskIdKeyHolder, new String[]{taskIdCol});

        int taskId = (Integer) taskIdKeyHolder.getKeys().get(taskIdCol);

        return taskBuilder.setTaskId(taskId)
                .build();
    }

    public PersonnelTask insertDummyTask() {
        return insertDummyTask(new PersonnelTaskTestBuilder());
    }

    public int setTaskActive(int taskId, boolean active) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("taskId", taskId)
                .addValue("active", active);
        return localNamedJdbc.update(setActiveSql, params);
    }
}
