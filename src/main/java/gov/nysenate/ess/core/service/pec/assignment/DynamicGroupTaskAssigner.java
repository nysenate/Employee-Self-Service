package gov.nysenate.ess.core.service.pec.assignment;

import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.dao.pec.task.PersonnelTaskDao;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignmentGroup;
import gov.nysenate.ess.core.service.pec.notification.AssignmentWithTask;
import gov.nysenate.ess.core.service.pec.task.PersonnelTaskService;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Used when we manually assign something, or respect assignment from e.g. Everfi.
 */
@Service
public class DynamicGroupTaskAssigner extends BaseGroupTaskAssigner {

    public DynamicGroupTaskAssigner(PersonnelTaskAssignmentDao assignmentDao,
                                   PersonnelTaskService taskService,
                                    EmployeeInfoService employeeInfoService,
                                    PersonnelTaskDao personnelTaskDao) {
        super(assignmentDao, taskService, employeeInfoService, personnelTaskDao);
    }

    @Override
    public PersonnelTaskAssignmentGroup getTargetGroup() {
        return PersonnelTaskAssignmentGroup.DYNAMIC;
    }

    @Override
    public List<AssignmentWithTask> assignGroupTasks(int empId, boolean updateDb) {
        return List.of();
    }

    @Override
    public Set<Integer> getRequiredTaskIds(int empId) {
        return Set.of();
    }
}
