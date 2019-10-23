package gov.nysenate.ess.core.client.view.pec;

import gov.nysenate.ess.core.model.pec.MoodleCourseTask;

import java.util.Objects;

public class MoodleTaskView extends PersonnelTaskView {

    private String url;

    public MoodleTaskView(MoodleCourseTask task) {
        super(task);
        this.url = Objects.toString(task.getCourseUrl());
    }

    public String getUrl() {
        return url;
    }
}
