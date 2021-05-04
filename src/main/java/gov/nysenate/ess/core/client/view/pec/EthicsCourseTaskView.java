package gov.nysenate.ess.core.client.view.pec;

import gov.nysenate.ess.core.model.pec.ethics.EthicsCourseTask;
import gov.nysenate.ess.core.model.pec.everfi.EverfiCourseTask;

import java.util.Objects;

public class EthicsCourseTaskView extends PersonnelTaskView {

    private String url;

    public EthicsCourseTaskView(EthicsCourseTask task) {
        super(task);
        this.url = Objects.toString(task.getCourseUrl());
    }

    public String getUrl() {
        return url;
    }
}
