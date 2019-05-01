package gov.nysenate.ess.core.dao.pec;

import gov.nysenate.ess.core.model.pec.PersonnelAssignedTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskId;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;

import java.util.StringJoiner;

/**
 * Object that holds values to pass into a query for {@link PersonnelAssignedTask}s.
 *
 * Unset/null fields will be ignored in the query.
 */
public class PATQueryBuilder {

    private Integer empId;
    private PersonnelTaskType taskType;
    private Integer taskNumber;
    private Boolean completed;

    @Override
    public String toString() {
        return new StringJoiner(", ", PATQueryBuilder.class.getSimpleName() + "[", "]")
                .add("empId=" + empId)
                .add("taskType=" + taskType)
                .add("taskNumber=" + taskNumber)
                .add("completed=" + completed)
                .toString();
    }

    /* --- Builder-style setters --- */

    public PATQueryBuilder setTaskId(PersonnelTaskId taskId) {
        this.taskType = taskId.getTaskType();
        this.taskNumber = taskId.getTaskNumber();
        return this;
    }

    public PATQueryBuilder setEmpId(Integer empId) {
        this.empId = empId;
        return this;
    }

    public PATQueryBuilder setTaskType(PersonnelTaskType taskType) {
        this.taskType = taskType;
        return this;
    }

    public PATQueryBuilder setTaskNumber(Integer taskNumber) {
        this.taskNumber = taskNumber;
        return this;
    }

    public PATQueryBuilder setCompleted(Boolean completed) {
        this.completed = completed;
        return this;
    }

    /* --- Getters --- */

    public Integer getEmpId() {
        return empId;
    }

    public PersonnelTaskType getTaskType() {
        return taskType;
    }

    public Integer getTaskNumber() {
        return taskNumber;
    }

    public Boolean getCompleted() {
        return completed;
    }
}
