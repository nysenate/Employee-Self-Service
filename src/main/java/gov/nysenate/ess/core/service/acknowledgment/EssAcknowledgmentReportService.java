package gov.nysenate.ess.core.service.acknowledgment;

import gov.nysenate.ess.core.model.pec.acknowledgment.EmpAckReport;

import java.util.ArrayList;

public interface EssAcknowledgmentReportService {

    /**
     * Returns a list of all acknowledgments for a single employee
     * This list of {@link EmpAckReport} will not contain any employees who have no acknowledgments
     *
     * @return {@link ArrayList<EmpAckReport>}
     */
    EmpAckReport getAllAcksFromEmployee(int empId);

    /**
     *All acks on a single document are reported as {@link EmpAckReport},
     * those who have not acked this document are included as well
     * @return {@link ArrayList<EmpAckReport>}
     */
    ArrayList<EmpAckReport> getAllAcksForAckDocById(int ackDocId);

}
