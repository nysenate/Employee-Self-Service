package gov.nysenate.ess.core.service.pec.assignment;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignmentGroup;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.service.pec.notification.PECNotificationService;
import gov.nysenate.ess.core.service.pec.task.PersonnelTaskService;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EthicsGroupTaskAssigner extends BaseGroupTaskAssigner {

    public EthicsGroupTaskAssigner(PersonnelTaskAssignmentDao assignmentDao,
                                   PersonnelTaskService taskService,
                                   PECNotificationService pecNotificationService,
                                   EmployeeInfoService employeeInfoService) {
        super(assignmentDao, taskService, pecNotificationService, employeeInfoService);
    }

    @Override
    public PersonnelTaskAssignmentGroup getTargetGroup() {
        return PersonnelTaskAssignmentGroup.ETHICS;
    }

    public Set<Integer> getRequiredTaskIds(int empId) {
        Optional<PersonnelTask> latestEthicsTaskOpt = getLatestEthicsTask();

        Optional<PersonnelTask> moodleTaskOpt = getMoodleEthicsTask();

        Optional<PersonnelTaskAssignment> latestCompletedOpt = getGroupAssignments(empId).stream()
                .filter(PersonnelTaskAssignment::isCompleted)
                .max(Comparator.comparing(PersonnelTaskAssignment::getUpdateTime));

        final Set<Integer> requiredTaskIds = new HashSet<>();

        if (latestCompletedOpt.isEmpty()) {
            moodleTaskOpt
                    .map(PersonnelTask::getTaskId)
                    .ifPresent(requiredTaskIds::add);
        }

        // If there is already a complete ethics task, see if there is a newer task to assign
        if (latestEthicsTaskOpt.isPresent() && latestCompletedOpt.isPresent()) {
            PersonnelTaskAssignment latestCompleted = latestCompletedOpt.get();
            PersonnelTask latestTask = latestEthicsTaskOpt.get();

            // Require the latest task if it was mandated after the last ethics assignment was completed.
            if (latestTask.getEffectiveDateTime().isAfter(latestCompleted.getUpdateTime())) {
                requiredTaskIds.add(latestTask.getTaskId());
            }
        } else {
            moodleTaskOpt
                    .map(PersonnelTask::getTaskId)
                    .ifPresent(requiredTaskIds::add);
        }

        return ImmutableSet.copyOf(requiredTaskIds);
    }

    private Optional<PersonnelTask> getMoodleEthicsTask() {
        List<PersonnelTask> moodleTasks = getActiveGroupTasks().stream()
                .filter(task -> task.getTaskType() == PersonnelTaskType.MOODLE_COURSE)
                .collect(Collectors.toList());
        return switch (moodleTasks.size()) {
            case 0 -> Optional.empty();
            case 1 -> Optional.of(moodleTasks.get(0));
            default -> throw new IllegalStateException(
                    "Expected a single moodle ethics task, got " + moodleTasks.size() + ": " + moodleTasks);
        };
    }

    private Optional<PersonnelTask> getLatestEthicsTask() {
        return getActiveGroupTasks().stream()
                .max(Comparator.comparing(PersonnelTask::getEffectiveDateTime));
    }

    private List<PersonnelTask> getNonMoodleEthicsTasks() {
        return getActiveGroupTasks().stream()
                .filter(task -> task.getTaskType() != PersonnelTaskType.MOODLE_COURSE)
                .collect(Collectors.toList());
    }

    private boolean effectiveAfterMoodleCompletion(PersonnelTask task, PersonnelTaskAssignment moodleAssignment) {
        return moodleAssignment.isCompleted() &&
                task.getEffectiveDateTime() != null &&
                task.getEffectiveDateTime().isAfter(moodleAssignment.getUpdateTime());
    }
}
