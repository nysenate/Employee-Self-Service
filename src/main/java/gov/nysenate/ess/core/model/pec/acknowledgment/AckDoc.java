package gov.nysenate.ess.core.model.pec.acknowledgment;

import gov.nysenate.ess.core.model.pec.PersonnelTask;

/**
 * A document that must be acknowledged by active employees.
 */
public class AckDoc extends PersonnelTask {

    private String filename;

    public AckDoc(PersonnelTask task) {
        super(task);
        this.filename = task.getResource();
    }

    public String getFilename() {
        return filename;
    }
}
