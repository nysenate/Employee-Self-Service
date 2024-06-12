package gov.nysenate.ess.core.model.pec.everfi;

import gov.nysenate.ess.core.model.pec.PersonnelTask;

import java.net.MalformedURLException;
import java.net.URL;

public class EverfiCourseTask extends PersonnelTask {

    private final URL courseUrl;

    public EverfiCourseTask(PersonnelTask task) {
        super(task);

        try {
            this.courseUrl = new URL(task.getUrl());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid moodle course url string: \"" + task.getUrl() + "\"", e);
        }
    }

    public URL getCourseUrl() {
        return courseUrl;
    }
}
