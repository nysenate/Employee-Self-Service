package gov.nysenate.ess.core.model.pec.acknowledgment;

import gov.nysenate.ess.core.model.pec.PersonnelTask;

/**
 * A document that must be acknowledged by active employees.
 */
public class AckDoc extends PersonnelTask {

    /** Path to document pdf */
    private String filename;

    public AckDoc(PersonnelTask task, String filename) {
        super(task);
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}
