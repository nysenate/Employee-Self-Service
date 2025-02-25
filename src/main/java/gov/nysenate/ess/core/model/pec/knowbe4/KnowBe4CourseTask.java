package gov.nysenate.ess.core.model.pec.knowbe4;

import gov.nysenate.ess.core.model.pec.PersonnelTask;

import java.net.MalformedURLException;
import java.net.URL;

public class KnowBe4CourseTask extends PersonnelTask {

    private final URL courseUrl;

    public KnowBe4CourseTask(PersonnelTask task) {
        super(task);

        try {
            this.courseUrl = new URL(task.getUrl());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid KnowBe4 course url string: \"" + task.getUrl() + "\"", e);
        }
    }

    public URL getCourseUrl() {
        return courseUrl;
    }
}
