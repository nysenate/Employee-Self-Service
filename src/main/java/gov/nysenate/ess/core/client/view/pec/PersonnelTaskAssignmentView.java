package gov.nysenate.ess.core.client.view.pec;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;

import java.time.LocalDateTime;

public class PersonnelTaskAssignmentView implements ViewObject {

    private int empId;
    private int taskId;
    private LocalDateTime timestamp;
    private Integer updateUserId;
    private boolean completed;
    private boolean active;

    public PersonnelTaskAssignmentView(int empId,
                                       int taskId,
                                       LocalDateTime timestamp,
                                       Integer updateUserId,
                                       boolean completed,
                                       boolean active) {
        this.empId = empId;
        this.taskId = taskId;
        this.timestamp = timestamp;
        this.updateUserId = updateUserId;
        this.completed = completed;
        this.active = active;
    }

    public PersonnelTaskAssignmentView(PersonnelTaskAssignment task) {
        this(
                task.getEmpId(),
                task.getTaskId(),
                task.getUpdateTime(),
                task.getUpdateEmpId(),
                task.isCompleted(),
                task.isActive()
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
