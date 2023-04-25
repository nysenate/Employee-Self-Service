package gov.nysenate.ess.core.model.pec;

import gov.nysenate.ess.core.util.DateUtils;

import java.time.LocalDateTime;

/**
 * Test class used for generating dummy {@link PersonnelTask}
 */
public class PersonnelTaskTestBuilder {

    private static int globalTaskTestNumber = 0;

    /**
     * Unique id for this builder instance
     */
    private final int testTaskNumber = globalTaskTestNumber++;

    private int taskId;
    private PersonnelTaskType taskType;
    private PersonnelTaskAssignmentGroup assignmentGroup;
    private String title;
    private LocalDateTime effectiveDateTime;
    private LocalDateTime endDateTime;
    private boolean active;

    private boolean notifiable;

    public PersonnelTaskTestBuilder(int taskId,
                                    PersonnelTaskType taskType,
                                    PersonnelTaskAssignmentGroup assignmentGroup,
                                    String title,
                                    LocalDateTime effectiveDateTime,
                                    LocalDateTime endDateTime,
                                    boolean active,
                                    boolean notifiable) {
        this.taskId = taskId;
        this.taskType = taskType;
        this.assignmentGroup = assignmentGroup;
        this.title = title;
        this.effectiveDateTime = effectiveDateTime;
        this.endDateTime = endDateTime;
        this.active = active;
        this.notifiable = notifiable;
    }

    public PersonnelTaskTestBuilder() {
        this(
                -1,
                PersonnelTaskType.DOCUMENT_ACKNOWLEDGMENT,
                PersonnelTaskAssignmentGroup.DEFAULT,
                null,
                DateUtils.LONG_AGO.atStartOfDay(),
                null,
                true,
                false
        );
        this.title = "This is a test task # " + this.testTaskNumber + ", please report to helpline if seen";
    }

    public PersonnelTaskTestBuilder(PersonnelTask task) {
        this(
                task.getTaskId(),
                task.getTaskType(),
                task.getAssignmentGroup(),
                task.getTitle(),
                task.getEffectiveDateTime(),
                task.getEndDateTime(),
                task.isActive(),
                task.isNotifiable()
        );
    }

    public PersonnelTask build() {
        return new PersonnelTask(
                taskId,
                taskType,
                assignmentGroup,
                title,
                effectiveDateTime,
                endDateTime,
                active,
                notifiable
        );
    }


    public PersonnelTaskTestBuilder setTaskId(Integer taskId) {
        this.taskId = taskId;
        return this;
    }

    public PersonnelTaskTestBuilder setTaskType(PersonnelTaskType taskType) {
        this.taskType = taskType;
        return this;
    }

    public PersonnelTaskTestBuilder setAssignmentGroup(PersonnelTaskAssignmentGroup assignmentGroup) {
        this.assignmentGroup = assignmentGroup;
        return this;
    }

    public PersonnelTaskTestBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public PersonnelTaskTestBuilder setEffectiveDateTime(LocalDateTime effectiveDateTime) {
        this.effectiveDateTime = effectiveDateTime;
        return this;
    }

    public PersonnelTaskTestBuilder setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
        return this;
    }

    public PersonnelTaskTestBuilder setActive(boolean active) {
        this.active = active;
        return this;
    }

    public int getTestTaskNumber() {
        return testTaskNumber;
    }

    public Integer getTaskId() {
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
}
