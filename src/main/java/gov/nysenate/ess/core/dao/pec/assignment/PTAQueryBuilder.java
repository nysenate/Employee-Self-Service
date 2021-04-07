package gov.nysenate.ess.core.dao.pec.assignment;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Object that holds values to pass into a query for {@link PersonnelTaskAssignment}s.
 *
 * Unset/null fields will be ignored in the query.
 */
public class PTAQueryBuilder {

    private Integer empId;
    private Boolean active;
    private Set<Integer> taskIds = null;
    private PersonnelTaskType taskType;
    private Boolean completed;
    private LocalDateTime completedFrom;
    private LocalDateTime completedTo;
    /**
     * Filters based on the completion status of all tasks for an employee
     * As opposed to the status of individual task results in {@link #completed}
     */
    private PTAQueryCompletionStatus totalCompletionStatus;

    @Override
    public String toString() {
        return new StringJoiner(", ", PTAQueryBuilder.class.getSimpleName() + "[", "]")
                .add("empId=" + empId)
                .add("active=" + active)
                .add("taskType=" + taskType)
                .add("completed=" + completed)
                .add("completedFrom=" + completedFrom)
                .add("completedTo=" + completedTo)
                .add("taskIds=" + taskIds)
                .add("totalCompletionStatus=" + totalCompletionStatus)
                .toString();
    }

    /* --- Builder-style setters --- */

    public PTAQueryBuilder setEmpId(Integer empId) {
        this.empId = empId;
        return this;
    }

    public PTAQueryBuilder setActive(Boolean active) {
        this.active = active;
        return this;
    }

    public PTAQueryBuilder setTaskType(PersonnelTaskType taskType) {
        this.taskType = taskType;
        return this;
    }

    public PTAQueryBuilder setCompleted(Boolean completed) {
        this.completed = completed;
        return this;
    }

    public PTAQueryBuilder setTaskIds(Collection<Integer> taskIds) {
        this.taskIds = Optional.ofNullable(taskIds)
                .map(ImmutableSet::copyOf)
                .orElse(null);
        return this;
    }

    public PTAQueryBuilder setCompletedFrom(LocalDateTime completedFrom) {
        this.completedFrom = completedFrom;
        return this;
    }

    public PTAQueryBuilder setCompletedTo(LocalDateTime completedTo) {
        this.completedTo = completedTo;
        return this;
    }

    public PTAQueryBuilder setTotalCompletionStatus(PTAQueryCompletionStatus totalCompletionStatus) {
        this.totalCompletionStatus = totalCompletionStatus;
        return this;
    }

    /* --- Getters --- */

    public Integer getEmpId() {
        return empId;
    }

    public PersonnelTaskType getTaskType() {
        return taskType;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public Set<Integer> getTaskIds() {
        return taskIds;
    }

    public Boolean getActive() {
        return active;
    }

    public LocalDateTime getCompletedFrom() {
        return completedFrom;
    }

    public LocalDateTime getCompletedTo() {
        return completedTo;
    }

    public PTAQueryCompletionStatus getTotalCompletionStatus() {
        return totalCompletionStatus;
    }
}
