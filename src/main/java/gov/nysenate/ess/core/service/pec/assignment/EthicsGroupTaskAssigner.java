package gov.nysenate.ess.core.service.pec.assignment;

import com.google.common.collect.Maps;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.pec.video.PersonnelTaskAssignmentGroup;
import gov.nysenate.ess.core.service.pec.task.PersonnelTaskService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EthicsGroupTaskAssigner extends BaseGroupTaskAssigner {

    public EthicsGroupTaskAssigner(PersonnelTaskAssignmentDao assignmentDao,
                                   PersonnelTaskService taskService) {
        super(assignmentDao, taskService);
    }

    @Override
    public PersonnelTaskAssignmentGroup getTargetGroup() {
        return PersonnelTaskAssignmentGroup.ETHICS;
    }

    @Override
    public int assignGroupTasks(int empId) {
        Set<Integer> validEthicsTaskIds = new HashSet<>();
        Optional<PersonnelTask> moodleTaskOpt = getMoodleEthicsTask();
        List<PersonnelTask> validNonMoodleTasks = getNonMoodleEthicsTasks();

        Map<Integer, PersonnelTaskAssignment> ethicsAssigns =
                Maps.uniqueIndex(getGroupAssignments(empId), PersonnelTaskAssignment::getTaskId);

        if (moodleTaskOpt.isPresent()) {
            final PersonnelTask moodleTask = moodleTaskOpt.get();
            validEthicsTaskIds.add(moodleTask.getTaskId());
            if (ethicsAssigns.containsKey(moodleTask.getTaskId())) {
                final PersonnelTaskAssignment moodleAssignment = ethicsAssigns.get(moodleTask.getTaskId());
                if (moodleAssignment.isCompleted()) {
                    validNonMoodleTasks.stream()
                            .filter(task -> effectiveAfterMoodleCompletion(task, moodleAssignment))
                            .map(PersonnelTask::getTaskId)
                            .forEach(validEthicsTaskIds::add);
                }
            }
        } else {
            validNonMoodleTasks.stream()
                    .map(PersonnelTask::getTaskId)
                    .forEach(validEthicsTaskIds::add);
        }

        return assignTasks(empId, validEthicsTaskIds);
    }

    private Optional<PersonnelTask> getMoodleEthicsTask() {
        List<PersonnelTask> moodleTasks = getActiveGroupTasks().stream()
                .filter(task -> task.getTaskType() == PersonnelTaskType.MOODLE_COURSE)
                .collect(Collectors.toList());
        switch (moodleTasks.size()) {
            case 0:
                return Optional.empty();
            case 1:
                return Optional.of(moodleTasks.get(0));
            default:
                throw new IllegalStateException(
                        "Expected a single moodle ethics task, got " + moodleTasks.size() + ": " + moodleTasks);
        }
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
