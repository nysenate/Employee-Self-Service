package gov.nysenate.ess.core.model.pec;

import com.google.common.base.Objects;

import java.net.URL;

/**
 * A task that requires the user to visit an external site to complete.
 *
 * Contains a url to that site.
 */
public class ExternalPersonnelTask extends SimplePersonnelTask {

    private final URL url;

    public ExternalPersonnelTask(PersonnelTaskId taskId, String title, boolean active, URL url) {
        super(taskId, title, active);
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExternalPersonnelTask)) return false;
        if (!super.equals(o)) return false;
        ExternalPersonnelTask that = (ExternalPersonnelTask) o;
        return Objects.equal(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), url);
    }

    public URL getUrl() {
        return url;
    }
}
