package gov.nysenate.ess.core.client.view.pec;

import gov.nysenate.ess.core.model.pec.ExternalPersonnelTask;

import java.util.Objects;

public class ExternalPersonnelTaskView extends SimplePersonnelTaskView {

    private String url;

    public ExternalPersonnelTaskView(ExternalPersonnelTask task) {
        super(task);
        this.url = Objects.toString(task.getUrl());
    }

    public String getUrl() {
        return url;
    }
}
