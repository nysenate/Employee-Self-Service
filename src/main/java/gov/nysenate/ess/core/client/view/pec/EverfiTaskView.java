package gov.nysenate.ess.core.client.view.pec;

import gov.nysenate.ess.core.model.pec.everfi.EverfiCourseTask;

import java.util.Objects;

public class EverfiTaskView extends PersonnelTaskView {

    public EverfiTaskView(EverfiCourseTask task) {
        super(task);
    }

    public String getUrl() {
        return super.getUrl();
    }
}
