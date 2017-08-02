package gov.nysenate.ess.supply.integration.synchronization;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.config.DatabaseConfig;
import gov.nysenate.ess.supply.synchronization.dao.SfmsSynchronizationProcedure;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


@Category(IntegrationTest.class)
@Transactional(value = DatabaseConfig.remoteTxManager)
public class SynchronizationProcedureIT extends BaseTest {

    @Autowired
    private SfmsSynchronizationProcedure synchronizationProcedure;

    @Test(expected = org.springframework.dao.DataAccessException.class)
    public void throwErrorIfLineItemsAreNotFound() {
        synchronizationProcedure.synchronizeRequisition(xml);
    }

    /**
     * Example XML representing a requisition. Missing Line Item elements.
     */
    private final String xml = "<?xml version=\"1.0\"?>\n" +
            "<Requisition>\n" +
            "  <requisitionId>2147483647</requisitionId>\n" +
            "  <customerId>11168</customerId>\n" +
            "  <destinationCode>D07001</destinationCode>\n" +
            "  <destinationTypeCode>W</destinationTypeCode>\n" +
            "  <issuerUid>tlarkin</issuerUid>\n" +
            "  <approvedDateTime>2017-06-27T12:04:18.66</approvedDateTime>\n" +
            "</Requisition>";
}
