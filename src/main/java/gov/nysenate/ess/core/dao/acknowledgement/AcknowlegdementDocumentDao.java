package gov.nysenate.ess.core.dao.acknowledgement;

import gov.nysenate.ess.core.model.acknowledgement.AckDoc;
import gov.nysenate.ess.core.model.acknowledgement.Acknowledgement;

import java.util.Set;

public interface AcknowlegdementDocumentDao {

    /**
     * Gets an AckDoc by its ID in the database
     *
     * @param ackDocId
     * @return {@link AckDoc}
     */
    AckDoc getAckDoc(int ackDocId);

    /**
     * Inserts and ackDoc into the database
     *
     * @param ackDoc
     */
    void insertAckDoc(AckDoc ackDoc);

    /**
     * Gets a set of all active AckDocs
     *
     * @return {@link Set<AckDoc>}
     */
    Set<AckDoc> getActiveAckDocs();

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
     * @return {@link Set<Acknowledgement>}
     */
    Set<Acknowledgement> getAllAcknowledgements();

}
