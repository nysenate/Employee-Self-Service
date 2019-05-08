package gov.nysenate.ess.core.client.view.pec;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.pec.PersonnelTaskId;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;

import java.util.Objects;

public class PersonnelTaskIdView implements ViewObject {

    private PersonnelTaskType taskType;
    private int taskNumber;

    protected PersonnelTaskIdView() {}

    public PersonnelTaskIdView(PersonnelTaskType taskType, int taskNumber) {
        this.taskType = Objects.requireNonNull(taskType);
        this.taskNumber = taskNumber;
    }

    public PersonnelTaskIdView(PersonnelTaskId personnelTaskId) {
        this(personnelTaskId.getTaskType(), personnelTaskId.getTaskNumber());
    }

    @JsonIgnore
    public PersonnelTaskId toPersonnelTaskId() {
        return new PersonnelTaskId(taskType, taskNumber);
    }

    @Override
    public String getViewType() {
        return "personnel-task-id";
    }

    public PersonnelTaskType getTaskType() {
        return taskType;
    }

    public int getTaskNumber() {
        return taskNumber;
    }
}
