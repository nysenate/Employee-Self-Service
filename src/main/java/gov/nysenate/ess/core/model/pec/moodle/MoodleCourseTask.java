package gov.nysenate.ess.core.model.pec.moodle;

import gov.nysenate.ess.core.model.pec.PersonnelTask;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * {@link PersonnelTask} to complete a course on moodle.
 */
public class MoodleCourseTask extends PersonnelTask {

    private final URL courseUrl;

    public MoodleCourseTask(PersonnelTask task) {
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
