package gov.nysenate.ess.core.model.pec;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * {@link PersonnelTask} to complete a course on moodle.
 */
public class MoodleCourseTask extends PersonnelTask {

    private final URL courseUrl;

    public MoodleCourseTask(PersonnelTask task, String urlString) {
        super(task);

        try {
            this.courseUrl = new URL(urlString);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid moodle course url string: \"" + urlString + "\"", e);
        }
    }

    public URL getCourseUrl() {
        return courseUrl;
    }
}
