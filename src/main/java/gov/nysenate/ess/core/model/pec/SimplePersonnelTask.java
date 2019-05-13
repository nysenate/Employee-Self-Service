package gov.nysenate.ess.core.model.pec;

/**
 * A task that doesn't contain any data outside of that defined in {@link PersonnelTask}
 */
public class SimplePersonnelTask implements PersonnelTask {

    private final PersonnelTaskId taskId;
    private final String title;

    public SimplePersonnelTask(PersonnelTaskId taskId, String title) {
        this.taskId = taskId;
        this.title = title;
    }

    @Override
    public PersonnelTaskId getTaskId() {
        return taskId;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
