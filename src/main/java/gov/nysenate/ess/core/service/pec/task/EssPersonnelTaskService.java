package gov.nysenate.ess.core.service.pec.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import gov.nysenate.ess.core.dao.pec.task.PersonnelTaskDao;
import gov.nysenate.ess.core.dao.pec.task.detail.PersonnelTaskDetailDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignmentGroup;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.service.RefreshedCachedData;
import gov.nysenate.ess.core.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implements {@link PersonnelTaskService} using {@link PersonnelTaskDao} and {@link PersonnelTaskDetailDao}
 * and utilizing caching to improve performance.
 */
public class EssPersonnelTaskService
        extends RefreshedCachedData<Integer, PersonnelTask>
        implements PersonnelTaskService {
    private final ImmutableMap<PersonnelTaskType, PersonnelTaskDetailDao<? extends PersonnelTask>> taskDetailDaoMap;

    @Autowired
    public EssPersonnelTaskService(PersonnelTaskDao taskDao, @Value("${cache.cron.task}") String cron,
                                   List<PersonnelTaskDetailDao<?>> taskDetailDaos) {
        super(cron, () -> CollectionUtils.valuesToMap(taskDao.getAllTasks(), PersonnelTask::getTaskId));
        this.taskDetailDaoMap = Maps.uniqueIndex(taskDetailDaos, PersonnelTaskDetailDao::taskType);
    }

    /* --- PersonnelTaskService implementations --- */

    @Override
    public Set<Integer> getAllTaskIds(boolean activeOnly) {
        return getPersonnelTasks(activeOnly, false).stream()
                .map(PersonnelTask::getTaskId)
                .collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public PersonnelTask getPersonnelTask(int taskId, boolean getDetail) throws PersonnelTaskNotFoundEx {
        PersonnelTask personnelTask = dataMap().get(taskId);
        if (personnelTask == null) {
            throw new PersonnelTaskNotFoundEx(taskId);
        }
        return getDetail ? getDetailedTask(personnelTask) : personnelTask;
    }

    @Override
    public List<PersonnelTask> getPersonnelTasks(boolean activeOnly, boolean getDetail) {
        Stream<PersonnelTask> taskStream = dataMap().values().stream()
                .filter(task -> !activeOnly || task.isActive());
        if (getDetail) {
            taskStream = taskStream.map(this::getDetailedTask);
        }
        return taskStream.sorted(Comparator.comparingInt(PersonnelTask::getTaskId))
                .collect(Collectors.toList());
    }

    @Override
    public List<PersonnelTask> getActiveTasksInGroup(PersonnelTaskAssignmentGroup assignmentGroup) {
        return getPersonnelTasks(true, false).stream()
                .filter(task -> task.getAssignmentGroup() == assignmentGroup).toList();
    }

    private PersonnelTask getDetailedTask(PersonnelTask basicTask) {
        return taskDetailDaoMap.get(basicTask.getTaskType()).getTaskDetails(basicTask);
    }
}
