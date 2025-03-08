package gov.nysenate.ess.core.client.view.pec;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;

import java.time.LocalDateTime;

public class PersonnelTaskAssignmentView implements ViewObject {

    private final int empId;
    private final int taskId;
    private final LocalDateTime timestamp;
    private final Integer updateUserId;
    private final boolean completed;
    private final boolean active;

    private final boolean manual_override;

    private final LocalDateTime assignmentDate;
    private final LocalDateTime dueDate;

    public PersonnelTaskAssignmentView(int empId,
                                       int taskId,
                                       LocalDateTime timestamp,
                                       Integer updateUserId,
                                       boolean completed,
                                       boolean active,
                                       boolean manual_override,
                                       LocalDateTime assignmentDate,
                                       LocalDateTime dueDate) {
        this.empId = empId;
        this.taskId = taskId;
        this.timestamp = timestamp;
        this.updateUserId = updateUserId;
        this.completed = completed;
        this.active = active;
        this.manual_override = manual_override;
        this.assignmentDate = assignmentDate;
        this.dueDate = dueDate;
    }

    public PersonnelTaskAssignmentView(PersonnelTaskAssignment task) {
        this(
                task.getEmpId(),
                task.getTaskId(),
                task.getUpdateTime(),
                task.getUpdateEmpId(),
                task.isCompleted(),
                task.isActive(),
                task.wasManuallyOverridden(),
                task.getAssignmentDate(),
                task.getDueDate()
        );
    }

    @Override
    public String getViewType() {
        return "personnel-assigned-task";
    }

    public int getEmpId() {
        return empId;
    }

    public int getTaskId() {
        return taskId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Integer getUpdateUserId() {
        return updateUserId;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isActive() {
        return active;
    }
}
