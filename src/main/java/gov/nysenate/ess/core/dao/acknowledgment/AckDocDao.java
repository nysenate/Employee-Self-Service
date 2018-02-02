package gov.nysenate.ess.core.dao.acknowledgment;

import gov.nysenate.ess.core.model.acknowledgment.AckDoc;
import gov.nysenate.ess.core.model.acknowledgment.AckDocNotFoundEx;
import gov.nysenate.ess.core.model.acknowledgment.Acknowledgment;
import gov.nysenate.ess.core.model.acknowledgment.EmpAckReport;

import java.util.List;

public interface AckDocDao {

    /**
     * Gets an AckDoc by its ID in the database
     *
     * @param ackDocId - the id of the AckDoc object
     * @return {@link AckDoc}
     * @throws AckDocNotFoundEx if no {@link AckDoc} can be found with the given id
     */
    AckDoc getAckDoc(int ackDocId) throws AckDocNotFoundEx;

    /**
     * Inserts and ackDoc into the database
     *
     * @param ackDoc - the ackDoc to be inserted into the database
     */
    void insertAckDoc(AckDoc ackDoc);

    /**
     * Gets a set of all active AckDocs
     *
     * @return {@link List<AckDoc>}
     */
    List<AckDoc> getActiveAckDocs();

    /**
     * Gets an Acknowledgment from an employee id and an ackDocId
     *
     * @param empId - the Id of the employee
     * @param ackDocId - the id of the ackDoc
     * @return {@link Acknowledgment} - the acknowledgment corresponding to the emp and the ackDoc
     */
    Acknowledgment getAcknowledgmentById(int empId, int ackDocId);

    /**
     * Inserts an Acknowledgment into the database
     *
     * @param acknowledgment - the Acknowledgment to be inserted into the database
     */
    void insertAcknowledgment(Acknowledgment acknowledgment);

    /**
     * Gets all acknowledgments in the database
     *
     * @return {@link List< Acknowledgment >}
     */
    List<Acknowledgment> getAllAcknowledgments();

    /**
     * Gets all acknowledgments in the database for a specified employee
     *
     * @return {@link List< Acknowledgment >}
     */
    List<Acknowledgment> getAllAcknowledgmentsForEmp(int empId);

    /**
     * This method returns all acknowledgments for the ackDoc with the reuqested id for all active employees.
     *
     * {@link EmpAckReport}
     * @param ackDocId - The id of the ack doc a personnel member would want to generate a report for
     * @return {@link List<EmpAckReport>} - The list of all acknowlegments in report form for the requested ackdoc
     */
    public List<EmpAckReport> getAllAcksForAckDocById(int ackDocId);

    /**
     * This method returns all acknowledgments across ackdocs for all active senate employees
     *
     * {@link EmpAckReport}
     * @return @return {@link List<EmpAckReport>} - The list of all acknowlegments in report form for the requested ackdoc
     */
    public List<EmpAckReport> getAllAcksForEmpWithTimestampAndDocRef();

}
