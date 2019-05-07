package gov.nysenate.ess.core.client.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.pec.PersonnelAssignedTask;

import java.time.LocalDateTime;

public class PersonnelAssignedTaskView implements ViewObject {

    private int empId;
    private PersonnelTaskIdView taskId;
    private LocalDateTime timestamp;
    private Integer updateUserId;
    private boolean completed;

    public PersonnelAssignedTaskView(int empId,
                                     PersonnelTaskIdView taskId,
                                     LocalDateTime timestamp,
                                     Integer updateUserId,
                                     boolean completed) {
        this.empId = empId;
        this.taskId = taskId;
        this.timestamp = timestamp;
        this.updateUserId = updateUserId;
        this.completed = completed;
    }

    public PersonnelAssignedTaskView(PersonnelAssignedTask task) {
        this(
                task.getEmpId(),
                new PersonnelTaskIdView(task.getTaskId()),
                task.getTimestamp(),
                task.getUpdateUserId(),
                task.isCompleted()
        );
    }

    @Override
    public String getViewType() {
        return "personnel-assigned-task";
    }

    public int getEmpId() {
        return empId;
    }

    public PersonnelTaskIdView getTaskId() {
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
}
