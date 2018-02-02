package gov.nysenate.ess.core.service.acknowledgment;

import gov.nysenate.ess.core.model.acknowledgment.EmpAckReport;

import java.util.ArrayList;

public interface EssAcknowledgmentReportService {

    /**
     * Returns a list of all acknowledgments for each employee
     * This list of {@link EmpAckReport} will not contain any employees who have no acknowledgments
     *
     * @return {@link ArrayList<EmpAckReport>}
     */
    public ArrayList<EmpAckReport> getAllAcksFromEmployees();

    /**
     *All acks on a single document are reported as {@link EmpAckReport},
     * those who have not acked this document are included as well
     * @return {@link ArrayList<EmpAckReport>}
     */
    public ArrayList<EmpAckReport> getAllAcksForAckDocById(int ackDocId);

}
