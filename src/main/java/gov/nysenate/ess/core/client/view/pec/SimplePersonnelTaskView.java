package gov.nysenate.ess.core.client.view.pec;

import gov.nysenate.ess.core.model.pec.SimplePersonnelTask;

public class SimplePersonnelTaskView implements PersonnelTaskView {

    private final PersonnelTaskIdView taskId;
    private final String title;

    public SimplePersonnelTaskView(SimplePersonnelTask task) {
        this.taskId = new PersonnelTaskIdView(task.getTaskId());
        this.title = task.getTitle();
    }

    @Override
    public PersonnelTaskIdView getTaskId() {
        return taskId;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
