package gov.nysenate.ess.core.client.view.pec;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.pec.PersonnelAssignedTask;

import java.time.LocalDateTime;

/**
 * Encapsulates only fields that can be directly set by users.
 * i.e. excludes update user and update timestamp fields.
 */
public class PersonnelAssignedTaskUpdateView implements ViewObject {

    private int empId;
    private PersonnelTaskIdView taskId;
    private boolean completed;

    protected PersonnelAssignedTaskUpdateView() {}

    /**
     * Generates a fully formed task using the missing parameters.
     *
     * @param updateEmpId int
     * @param timestamp LocalDateTime
     * @return {@link PersonnelAssignedTask}
     */
    public PersonnelAssignedTask toPersonnelAssignedTask(int updateEmpId, LocalDateTime timestamp) {
        return new PersonnelAssignedTask(
                empId,
                taskId.toPersonnelTaskId(),
                timestamp,
                updateEmpId,
                completed
        );
    }

    @Override
    public String getViewType() {
        return "personnel-assigned-task-update";
    }

    public int getEmpId() {
        return empId;
    }

    public PersonnelTaskIdView getTaskId() {
        return taskId;
    }

    public boolean isCompleted() {
        return completed;
    }
}
