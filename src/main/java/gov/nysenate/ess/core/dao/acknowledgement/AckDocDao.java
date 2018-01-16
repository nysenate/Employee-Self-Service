package gov.nysenate.ess.core.dao.acknowledgement;

import gov.nysenate.ess.core.model.acknowledgement.AckDoc;
import gov.nysenate.ess.core.model.acknowledgement.AckDocNotFoundEx;
import gov.nysenate.ess.core.model.acknowledgement.Acknowledgement;

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
     * Gets an Acknowledgement from an employee id and an ackDocId
     *
     * @param empId
     * @param ackDocId
     * @return {@link Acknowledgement}
     */
    Acknowledgement getAcknowledgementById(int empId, int ackDocId);

    /**
     * Inserts an Acknowledgement into the database
     *
     * @param acknowledgement
     */
    void insertAcknowledgement(Acknowledgement acknowledgement);

    /**
     * Gets all acknowledgements in the database
     *
     * @return {@link List<Acknowledgement>}
     */
    List<Acknowledgement> getAllAcknowledgements();

}
