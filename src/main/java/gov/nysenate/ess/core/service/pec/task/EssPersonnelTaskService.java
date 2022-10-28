package gov.nysenate.ess.core.service.pec.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import gov.nysenate.ess.core.dao.pec.task.PersonnelTaskDao;
import gov.nysenate.ess.core.dao.pec.task.detail.PersonnelTaskDetailDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignmentGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implements {@link PersonnelTaskService} using {@link PersonnelTaskDao} and {@link PersonnelTaskDetailDao}
 * and utilizing caching to improve performance.
 */
@Service
public class EssPersonnelTaskService implements PersonnelTaskService {
    private final PersonnelTaskDao taskDao;
    private final ImmutableMap<PersonnelTaskType, PersonnelTaskDetailDao<?>> taskDetailDaoMap;
    private ImmutableMap<Integer, PersonnelTask> taskMap;

    @Autowired
    public EssPersonnelTaskService(PersonnelTaskDao taskDao,
                                   List<PersonnelTaskDetailDao<?>> taskDetailDaos) {
        this.taskDao = taskDao;
        this.taskDetailDaoMap = Maps.uniqueIndex(taskDetailDaos, PersonnelTaskDetailDao::taskType);
        initializeData();
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
        PersonnelTask personnelTask = taskMap.get(taskId);
        if (personnelTask == null) {
            throw new PersonnelTaskNotFoundEx(taskId);
        }
        return getDetail ? personnelTask : getDetailedTask(personnelTask);
    }

    @Override
    public List<PersonnelTask> getPersonnelTasks(boolean activeOnly, boolean getDetail) {
        Stream<PersonnelTask> taskStream = taskMap.values().stream()
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

    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedDelay = 5)
    private void initializeData() {
        taskMap = taskDao.getAllTasks().stream()
                .collect(ImmutableMap.toImmutableMap(PersonnelTask::getTaskId, Function.identity()));
    }
}
