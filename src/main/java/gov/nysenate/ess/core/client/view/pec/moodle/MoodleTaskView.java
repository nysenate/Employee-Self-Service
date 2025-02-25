package gov.nysenate.ess.core.client.view.pec.moodle;

import gov.nysenate.ess.core.client.view.pec.PersonnelTaskView;
import gov.nysenate.ess.core.model.pec.PersonnelTask;

public class MoodleTaskView extends PersonnelTaskView {

    private String url;

    public MoodleTaskView(PersonnelTask task) {
        super(task);
    }

    public String getUrl() {
        return super.getUrl();
    }
}
