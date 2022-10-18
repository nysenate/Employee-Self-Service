package gov.nysenate.ess.core.service.pec.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import gov.nysenate.ess.core.dao.pec.task.PersonnelTaskDao;
import gov.nysenate.ess.core.dao.pec.task.detail.PersonnelTaskDetailDao;
import gov.nysenate.ess.core.model.cache.CacheType;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.pec.video.PersonnelTaskAssignmentGroup;
import gov.nysenate.ess.core.service.cache.UnclearableCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implements {@link PersonnelTaskService} using {@link PersonnelTaskDao} and {@link PersonnelTaskDetailDao}
 * and utilizing caching to improve performance.
 */
@Service
public class CachedPersonnelTaskService extends UnclearableCache<Integer, PersonnelTask>
        implements PersonnelTaskService {
    private final PersonnelTaskDao taskDao;
    private final ImmutableMap<PersonnelTaskType, PersonnelTaskDetailDao<?>> taskDetailDaoMap;

    @Autowired
    public CachedPersonnelTaskService(PersonnelTaskDao taskDao,
                                      List<PersonnelTaskDetailDao<?>> taskDetailDaos) {
        this.taskDao = taskDao;
        this.taskDetailDaoMap = Maps.uniqueIndex(taskDetailDaos, PersonnelTaskDetailDao::taskType);
    }

    @Override
    protected Map<Integer, PersonnelTask> initialEntries() {
        return taskDao.getAllTasks().stream().map(this::getDetailedTask)
                .collect(Collectors.toMap(PersonnelTask::getTaskId, Function.identity()));
    }

    /* --- PersonnelTaskService implementations --- */

    @Override
    public Set<Integer> getAllTaskIds(boolean activeOnly) {
        return getPersonnelTasks(activeOnly, false).stream()
                .map(PersonnelTask::getTaskId)
                .collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public synchronized PersonnelTask getPersonnelTask(int taskId, boolean getDetail) throws PersonnelTaskNotFoundEx {
        PersonnelTask personnelTask = cache.get(taskId);
        if (personnelTask == null) {
            throw new PersonnelTaskNotFoundEx(taskId);
        }
        return getDetail ? personnelTask : getDetailedTask(personnelTask);
    }

    @Override
    public synchronized List<PersonnelTask> getPersonnelTasks(boolean activeOnly, boolean getDetail) {
        var iter = cache.iterator();
        List<PersonnelTask> list = new ArrayList<>();
        while (iter.hasNext()) {
            PersonnelTask task = iter.next().getValue();
            if (!activeOnly || task.isActive()) {
                list.add(task);
            }
        }
        if (getDetail) {
            list = list.stream().map(this::getDetailedTask).collect(Collectors.toList());
        }
        list.sort(Comparator.comparingInt(PersonnelTask::getTaskId));
        return list;
    }

    @Override
    public List<PersonnelTask> getActiveTasksInGroup(PersonnelTaskAssignmentGroup assignmentGroup) {
        return getPersonnelTasks(true, false).stream()
                .filter(task -> task.getAssignmentGroup() == assignmentGroup).toList();
    }

    /* --- CachingService implementations --- */

    @Override
    public CacheType cacheType() {
        return CacheType.PERSONNEL_TASK;
    }

    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedDelay = 5)
    private void remakeCache() {
        clearCache(true);
    }

    private PersonnelTask getDetailedTask(PersonnelTask basicTask) {
        return taskDetailDaoMap.get(basicTask.getTaskType()).getTaskDetails(basicTask);
    }
}
