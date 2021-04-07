package gov.nysenate.ess.core.client.view.pec;

import gov.nysenate.ess.core.model.pec.everfi.EverfiCourseTask;

import java.util.Objects;

public class EverfiTaskView extends PersonnelTaskView {

    private String url;

    public EverfiTaskView(EverfiCourseTask task) {
        super(task);
        this.url = Objects.toString(task.getCourseUrl());
    }

    public String getUrl() {
        return url;
    }
}
