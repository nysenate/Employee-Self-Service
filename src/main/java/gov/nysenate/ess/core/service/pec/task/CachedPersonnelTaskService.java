package gov.nysenate.ess.core.service.pec.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implements {@link PersonnelTaskService} using {@link PersonnelTaskDao} and {@link PersonnelTaskDetailDao}
 * and utilizing caching to improve performance.
 */
@Service
public class CachedPersonnelTaskService extends CachingService<Integer, PersonnelTask>
        implements PersonnelTaskService {

    private static final Logger logger = LoggerFactory.getLogger(CachedPersonnelTaskService.class);
    /** Number of seconds the task map is valid in cache before it is evicted */
    private static final int cacheEvictTime = 300;

    private final PersonnelTaskDao taskDao;
    private final ImmutableMap<PersonnelTaskType, PersonnelTaskDetailDao<?>> taskDetailDaoMap;
    // TODO: move these two into PersonnelTaskAdminApiCtrl
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
    public PersonnelTask getPersonnelTask(int taskId, boolean getDetail) throws PersonnelTaskNotFoundEx {
        PersonnelTask personnelTask = cache.get(taskId);
        if (personnelTask == null) {
            throw new PersonnelTaskNotFoundEx(taskId);
        }
        return getDetail ? personnelTask : getDetailedTask(personnelTask);
    }

    @Override
    public List<PersonnelTask> getPersonnelTasks(boolean activeOnly, boolean getDetail) {
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

    @Override
    public void evictContent(Integer key) {
        super.clearCache(true);
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

    private PersonnelTask getDetailedTask(PersonnelTask basicTask) {
        return taskDetailDaoMap.get(basicTask.getTaskType()).getTaskDetails(basicTask);
    }
}
