package gov.nysenate.ess.core.model.pec;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

import java.time.LocalDateTime;
import java.util.StringJoiner;

import static java.util.Objects.requireNonNull;

/**
 * A general task assigned to employees by personnel
 */
public class PersonnelTask implements Comparable<PersonnelTask> {
    private final int taskId;
    private final PersonnelTaskType taskType;
    private final PersonnelTaskAssignmentGroup assignmentGroup;
    private final String title;
    private final LocalDateTime effectiveDateTime;
    private final LocalDateTime endDateTime;
    private final boolean active;
    private final boolean notifiable;

    public PersonnelTask(int taskId,
                         PersonnelTaskType taskType,
                         PersonnelTaskAssignmentGroup assignmentGroup,
                         String title,
                         LocalDateTime effectiveDateTime,
                         LocalDateTime endDateTime,
                         boolean active,
                         boolean notifiable) {
        this.taskId = taskId;
        this.taskType = requireNonNull(taskType);
        this.assignmentGroup = assignmentGroup;
        this.title = requireNonNull(title);
        this.effectiveDateTime = effectiveDateTime;
        this.endDateTime = endDateTime;
        this.active = active;
        this.notifiable = notifiable;
    }

    public PersonnelTask(PersonnelTask other) {
        this(
                other.taskId,
                other.taskType,
                other.assignmentGroup,
                other.title,
                other.effectiveDateTime,
                other.endDateTime,
                other.active,
                other.notifiable
        );
    }

    /* --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonnelTask that)) return false;
        return taskId == that.taskId &&
                active == that.active &&
                taskType == that.taskType &&
                assignmentGroup == that.assignmentGroup &&
                Objects.equal(title, that.title) &&
                Objects.equal(effectiveDateTime, that.effectiveDateTime) &&
                Objects.equal(endDateTime, that.endDateTime) &&
                Objects.equal(notifiable, that.notifiable);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(taskId, taskType, assignmentGroup, title, effectiveDateTime, endDateTime, active, notifiable);
    }

    @Override
    public int compareTo(PersonnelTask o) {
        return ComparisonChain.start()
                .compare(this.taskType, o.taskType)
                .compare(this.title, o.title)
                .result();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PersonnelTask.class.getSimpleName() + "[", "]")
                .add("taskId=" + taskId)
                .add("taskType=" + taskType)
                .add("assignmentGroup=" + assignmentGroup)
                .add("title='" + title + "'")
                .add("effectiveDateTime=" + effectiveDateTime)
                .add("endDateTime=" + endDateTime)
                .add("active=" + active)
                .add("notfiable=" + notifiable)
                .toString();
    }

    /* --- Getters --- */

    public int getTaskId() {
        return taskId;
    }

    public PersonnelTaskType getTaskType() {
        return taskType;
    }

    public PersonnelTaskAssignmentGroup getAssignmentGroup() {
        return assignmentGroup;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getEffectiveDateTime() {
        return effectiveDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isNotifiable() {
        return notifiable;
    }
}
