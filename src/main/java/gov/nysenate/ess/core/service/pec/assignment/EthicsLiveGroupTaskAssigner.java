package gov.nysenate.ess.core.service.pec.assignment;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.dao.pec.task.PersonnelTaskDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignmentGroup;
import gov.nysenate.ess.core.service.pec.task.PersonnelTaskService;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class EthicsLiveGroupTaskAssigner extends BaseGroupTaskAssigner {

    public EthicsLiveGroupTaskAssigner(PersonnelTaskAssignmentDao assignmentDao,
                                       PersonnelTaskService taskService,
                                       EmployeeInfoService employeeInfoService,
                                       PersonnelTaskDao personnelTaskDao) {
        super(assignmentDao, taskService, employeeInfoService, personnelTaskDao);
    }

    @Override
    public PersonnelTaskAssignmentGroup getTargetGroup() {
        return PersonnelTaskAssignmentGroup.ETHICS_LIVE;
    }

    public Set<Integer> getRequiredTaskIds(int empId) {
        //Get the latest task
        Optional<PersonnelTask> latestEthicsLiveTaskOpt = getLatestEthicsTask();
        //See what the employees latest completed task was
        Optional<PersonnelTaskAssignment> latestCompletedOpt = getGroupAssignments(empId).stream()
                .filter(PersonnelTaskAssignment::isCompleted)
                .max(Comparator.comparing(PersonnelTaskAssignment::getUpdateTime));

        LocalDateTime beginningOfCalendarYear = LocalDateTime.of(LocalDate.now().getYear(), 1,1,0,0,0);
        final Set<Integer> requiredTaskIds = new HashSet<>();

        if (latestCompletedOpt.isPresent()) {
            PersonnelTaskAssignment latestCompletedTask = latestCompletedOpt.get();

            if (latestEthicsLiveTaskOpt.isPresent() ) {
                PersonnelTask latestTask = latestEthicsLiveTaskOpt.get();

                // Require the latest task if it was mandated after the last ethics assignment was completed
                // AND they didnt complete the previous ethics live course in the current calendar year
                if ( latestTask.getEffectiveDateTime().isAfter(latestCompletedTask.getUpdateTime())
                        && latestCompletedTask.getUpdateTime().isBefore(beginningOfCalendarYear) ) {
                    requiredTaskIds.add(latestTask.getTaskId());
                }
            }
        }
        else {
            //its possible for people to have a deactivated ethics live.
            // so we need to check if it was assigned but deactivated
            Set<Integer> assignedEthicsLiveTasks = super.getAssignedIds(empId);

            if (assignedEthicsLiveTasks.isEmpty()) {
                //This means they dont have a completed task and no previously assigned deactivate task
                latestEthicsLiveTaskOpt
                        .map(PersonnelTask::getTaskId)
                        .ifPresent(requiredTaskIds::add);
            }
            else {
                Map<Integer, PersonnelTaskAssignment> assignmentMap = super.getAssignmentMap(empId);
                for(Integer taskID: assignedEthicsLiveTasks) {
                    PersonnelTaskAssignment taskAssignment = assignmentMap.get(taskID);
                    if (taskID != latestEthicsLiveTaskOpt.get().getTaskId() || taskAssignment.isActive()) {
                        latestEthicsLiveTaskOpt
                                .map(PersonnelTask::getTaskId)
                                .ifPresent(requiredTaskIds::add);
                    }
                }
            }
        }

        return ImmutableSet.copyOf(requiredTaskIds);
    }

    private Optional<PersonnelTask> getLatestEthicsTask() {
        return getActiveGroupTasks().stream()
                .max(Comparator.comparing(PersonnelTask::getEffectiveDateTime));
    }
}

