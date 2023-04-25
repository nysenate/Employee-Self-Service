package gov.nysenate.ess.core.service.pec.assignment;

import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignmentGroup;
import gov.nysenate.ess.core.service.pec.task.PersonnelTaskService;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DefaultGroupTaskAssigner extends BaseGroupTaskAssigner {

    public DefaultGroupTaskAssigner(PersonnelTaskAssignmentDao assignmentDao,
                                    PersonnelTaskService taskService,
                                    EmployeeInfoService employeeInfoService) {
        super(assignmentDao, taskService, employeeInfoService);
    }

    @Override
    public PersonnelTaskAssignmentGroup getTargetGroup() {
        return PersonnelTaskAssignmentGroup.DEFAULT;
    }

    @Override
    public Set<Integer> getRequiredTaskIds(int empId) {
        return getActiveGroupTasks().stream()
                .map(PersonnelTask::getTaskId)
                .collect(Collectors.toSet());
    }
}
