package gov.nysenate.ess.core.model.pec.ethics;

import gov.nysenate.ess.core.model.pec.PersonnelTask;

import java.net.MalformedURLException;
import java.net.URL;

public class EthicsCourseTask extends PersonnelTask {

    private final URL courseUrl;

    public EthicsCourseTask(PersonnelTask task) {
        super(task);

        try {
            this.courseUrl = new URL(task.getUrl());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid ethics course url string: \"" + task.getUrl() + "\"", e);
        }
    }

    public URL getCourseUrl() {
        return courseUrl;
    }

}
