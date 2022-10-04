package gov.nysenate.ess.core.service.pec.task;

import com.google.common.collect.*;
import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.dao.pec.task.PersonnelTaskDao;
import gov.nysenate.ess.core.dao.pec.task.detail.PersonnelTaskDetailDao;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.cache.CacheType;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.pec.video.PersonnelTaskAssignmentGroup;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.base.CachingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Implements {@link PersonnelTaskService} using {@link PersonnelTaskDao} and {@link PersonnelTaskDetailDao}
 * and utilizing caching to improve performance.
 */
@Service
public class CachedPersonnelTaskService
        extends CachingService<String, ImmutableSortedMap<Integer, PersonnelTask>>
        implements PersonnelTaskService {

    private static final Logger logger = LoggerFactory.getLogger(CachedPersonnelTaskService.class);

    /** Key used to store a map of all tasks */
    private static final String TASK_MAP_KEY = "TASK_MAP_KEY";
    /** Number of seconds the task map is valid in cache before it is evicted */
    private static final int cacheEvictTime = 300;

    private final PersonnelTaskDao taskDao;
    private final ImmutableMap<PersonnelTaskType, PersonnelTaskDetailDao<?>> taskDetailDaoMap;
    private final EmployeeDao employeeDao;
    private final PersonnelTaskAssignmentDao personnelTaskAssignmentDao;

    @Autowired
    public CachedPersonnelTaskService(PersonnelTaskDao taskDao,
                                      List<PersonnelTaskDetailDao<?>> taskDetailDaos,
                                      EmployeeDao employeeDao,
                                      PersonnelTaskAssignmentDao personnelTaskAssignmentDao) {
        this.taskDao = taskDao;
        this.taskDetailDaoMap = Maps.uniqueIndex(taskDetailDaos, PersonnelTaskDetailDao::taskType);
        this.employeeDao = employeeDao;
        this.personnelTaskAssignmentDao = personnelTaskAssignmentDao;
    }

    @Override
    public int expiryTimeSeconds() {
        return cacheEvictTime;
    }

    @Override
    protected Map<String, ImmutableSortedMap<Integer, PersonnelTask>> initialEntries() {
        ImmutableSortedMap<Integer, PersonnelTask> map = taskDao.getAllTasks().stream()
                .map(task -> taskDetailDaoMap.get(task.getTaskType()).getTaskDetails(task))
                .collect(ImmutableSortedMap.toImmutableSortedMap(
                        Comparable::compareTo, PersonnelTask::getTaskId, Function.identity()));
        return Map.of(TASK_MAP_KEY, map);
    }

    /* --- PersonnelTaskService implementations --- */

    @Override
    public Set<Integer> getAllTaskIds(boolean activeOnly) {
        return getPersonnelTasks(activeOnly).stream()
                .map(PersonnelTask::getTaskId)
                .collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public PersonnelTask getPersonnelTask(int taskId) throws PersonnelTaskNotFoundEx {
        PersonnelTask personnelTask = getTaskMap().get(taskId);
        if (personnelTask == null) {
            throw new PersonnelTaskNotFoundEx(taskId);
        }
        return personnelTask;
    }

    @Override
    public List<PersonnelTask> getPersonnelTasks(boolean activeOnly) {
        return getTaskMap().values().stream()
                .filter(task -> !activeOnly || task.isActive()).toList();
    }

    @Override
    public List<PersonnelTask> getActiveTasksInGroup(PersonnelTaskAssignmentGroup assignmentGroup) {
        return getPersonnelTasks(true).stream()
                .filter(task -> task.getAssignmentGroup() == assignmentGroup).toList();
    }

    /* --- CachingService implementations --- */

    @Override
    public CacheType cacheType() {
        return CacheType.PERSONNEL_TASK;
    }

    @Override
    public void evictContent(String key) {
        // Removing all since there is only one key
        super.clearCache(false);
    }

    public void markTasksComplete() {
        logger.info("Beginning the process of marking specific employees tasks complete");
        Set<Employee> employees = employeeDao.getActiveEmployees();
        Set<Employee> employeesToMarkComplete = new HashSet<>();
        employeesToMarkComplete.add(employeeDao.getEmployeeById(7689));
        employeesToMarkComplete.add(employeeDao.getEmployeeById(9268));
        employeesToMarkComplete.add(employeeDao.getEmployeeById(12867));

        for (Employee employee : employees) {
            if (employee.isSenator()) {
                employeesToMarkComplete.add(employee);
            }
        }

        for (Employee employee : employeesToMarkComplete) {
            List<PersonnelTaskAssignment> assignments =
                    personnelTaskAssignmentDao.getAssignmentsForEmp(employee.getEmployeeId());
            for (PersonnelTaskAssignment assignment : assignments) {
                if (!assignment.isCompleted()) {
                    personnelTaskAssignmentDao.setTaskComplete(
                            employee.getEmployeeId(), assignment.getTaskId(), employee.getEmployeeId());
                }
            }
        }
        logger.info("Finished the process of marking specific employees tasks complete");
    }

    /* --- Internal Methods --- */

    private ImmutableSortedMap<Integer, PersonnelTask> getTaskMap() {
        var value = cache.get(TASK_MAP_KEY);
        if (value == null) {
            clearCache(true);
        }
        return value;
    }
}
