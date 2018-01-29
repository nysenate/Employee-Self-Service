package gov.nysenate.ess.core.dao.acknowledgment;

import gov.nysenate.ess.core.model.acknowledgment.AckDoc;
import gov.nysenate.ess.core.model.acknowledgment.AckDocNotFoundEx;
import gov.nysenate.ess.core.model.acknowledgment.Acknowledgment;

import java.util.List;

public interface AckDocDao {

    /**
     * Gets an AckDoc by its ID in the database
     *
     * @param ackDocId
     * @return {@link AckDoc}
     * @throws AckDocNotFoundEx if no {@link AckDoc} can be found with the given id
     */
    AckDoc getAckDoc(int ackDocId) throws AckDocNotFoundEx;

    /**
     * Inserts and ackDoc into the database
     *
     * @param ackDoc
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
     * @param empId
     * @param ackDocId
     * @return {@link Acknowledgment}
     */
    Acknowledgment getAcknowledgmentById(int empId, int ackDocId);

    /**
     * Inserts an Acknowledgment into the database
     *
     * @param acknowledgment
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

}
