package gov.nysenate.ess.core.service.acknowledgment;

import gov.nysenate.ess.core.model.acknowledgment.AckDoc;
import gov.nysenate.ess.core.model.acknowledgment.EmpAckReport;

import java.util.ArrayList;
import java.util.List;

public interface EssAcknowledgmentReportService {

    /**
     * Returns a list of all acknowledgments for a single employee
     * This list of {@link EmpAckReport} will not contain any employees who have no acknowledgments
     *
     * @return {@link ArrayList<EmpAckReport>}
     */
    public EmpAckReport getAllAcksFromEmployee(int empId);

    /**
     *All acks on a single document are reported as {@link EmpAckReport},
     * those who have not acked this document are included as well
     * @return {@link ArrayList<EmpAckReport>}
     */
    public ArrayList<EmpAckReport> getAllAcksForAckDocById(int ackDocId);

    /**
     *Returns a list of Strings corresponding to years there are ack docs on record for
     * @return {@link List<String>}
     */
    public List<String> getAllYearsContainingAckDocs();

    /**
     *All Ack docs in a specified year are returned
     * @return {@link List<AckDoc>}
     */
    public List<AckDoc> getAllAckDocsInASpecificYear(int year);

}
