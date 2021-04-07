package gov.nysenate.ess.core.service.pec.assignment;

import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.video.PersonnelTaskAssignmentGroup;
import gov.nysenate.ess.core.service.pec.task.PersonnelTaskService;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DefaultGroupTaskAssigner extends BaseGroupTaskAssigner {

    public DefaultGroupTaskAssigner(PersonnelTaskAssignmentDao assignmentDao,
                                    PersonnelTaskService taskService) {
        super(assignmentDao, taskService);
    }

    @Override
    public PersonnelTaskAssignmentGroup getTargetGroup() {
        return PersonnelTaskAssignmentGroup.DEFAULT;
    }

    @Override
    public int assignGroupTasks(int empId) {
        Set<Integer> activeTaskIds = getActiveDefaultTaskIds();

        return assignTasks(empId, activeTaskIds);
    }

    private Set<Integer> getActiveDefaultTaskIds() {
        return getActiveGroupTasks().stream()
                .map(PersonnelTask::getTaskId)
                .collect(Collectors.toSet());
    }
}
