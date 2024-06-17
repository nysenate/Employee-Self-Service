package gov.nysenate.ess.core.client.view.pec;

import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.moodle.MoodleCourseTask;

import java.util.Objects;

public class MoodleTaskView extends PersonnelTaskView {

    private String url;

    public MoodleTaskView(PersonnelTask task) {
        super(task);
    }

    public String getUrl() {
        return super.getUrl();
    }
}
