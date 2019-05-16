package gov.nysenate.ess.core.model.pec;

import com.google.common.base.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimplePersonnelTask)) return false;
        SimplePersonnelTask that = (SimplePersonnelTask) o;
        return Objects.equal(taskId, that.taskId) &&
                Objects.equal(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(taskId, title);
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
