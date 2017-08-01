package gov.nysenate.ess.supply.integration.synchronization;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.config.DatabaseConfig;
import gov.nysenate.ess.supply.requisition.service.RequisitionService;
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
            "<RequisitionView>\n" +
            "  <requisitionId>2147483647</requisitionId>\n" +
            "  <revisionId>5</revisionId>\n" +
            "  <customer>\n" +
            "    <employeeId>11168</employeeId>\n" +
            "    <uid>caseiras</uid>\n" +
            "    <firstName>Kevin</firstName>\n" +
            "    <lastName>Caseiras</lastName>\n" +
            "    <fullName>Kevin F. Caseiras</fullName>\n" +
            "    <email>c@n.gov</email>\n" +
            "    <active>true</active>\n" +
            "    <title>Mr.</title>\n" +
            "    <initial>F.</initial>\n" +
            "    <suffix/>\n" +
            "    <workPhone>123-4567</workPhone>\n" +
            "    <homePhone>123-4567</homePhone>\n" +
            "    <gender>M</gender>\n" +
            "  </customer>\n" +
            "  <destination>\n" +
            "    <locId>A416F-W</locId>\n" +
            "    <code>A416F</code>\n" +
            "    <locationType>Work Location</locationType>\n" +
            "    <locationTypeCode>W</locationTypeCode>\n" +
            "    <address>\n" +
            "      <addr1>16th Floor, Agency Bldg 4</addr1>\n" +
            "      <addr2/>\n" +
            "      <city>Albany</city>\n" +
            "      <state>NY</state>\n" +
            "      <zip5>12247</zip5>\n" +
            "      <zip4/>\n" +
            "    </address>\n" +
            "    <respCenterHead>\n" +
            "      <active>true</active>\n" +
            "      <code>MAJSENIORS</code>\n" +
            "      <shortName>MAJ LD SR STAFF</shortName>\n" +
            "      <name>Majority Leader's Senior Staff</name>\n" +
            "      <affiliateCode>MAJ</affiliateCode>\n" +
            "    </respCenterHead>\n" +
            "    <locationDescription>16TH FLOOR, AGE</locationDescription>\n" +
            "  </destination>\n" +
            "  <deliveryMethod>DELIVERY</deliveryMethod>\n" +
            "  <specialInstructions/>\n" +
            "  <status>APPROVED</status>\n" +
            "  <issuer>\n" +
            "    <employeeId>11591</employeeId>\n" +
            "    <uid>tla</uid>\n" +
            "    <firstName>T</firstName>\n" +
            "    <lastName>La</lastName>\n" +
            "    <fullName>T La</fullName>\n" +
            "    <email>tla@n.gov</email>\n" +
            "    <active>true</active>\n" +
            "    <title>Mr.</title>\n" +
            "    <initial></initial>\n" +
            "    <suffix/>\n" +
            "    <workPhone>123-4567</workPhone>\n" +
            "    <homePhone>123-4567</homePhone>\n" +
            "    <gender>M</gender>\n" +
            "  </issuer>\n" +
            "  <note/>\n" +
            "  <modifiedBy>\n" +
             "    <employeeId>11591</employeeId>\n" +
            "    <uid>tla</uid>\n" +
            "    <firstName>T</firstName>\n" +
            "    <lastName>La</lastName>\n" +
            "    <fullName>T La</fullName>\n" +
            "    <email>tla@n.gov</email>\n" +
            "    <active>true</active>\n" +
            "    <title>Mr.</title>\n" +
            "    <initial></initial>\n" +
            "    <suffix/>\n" +
            "    <workPhone>123-4567</workPhone>\n" +
            "    <homePhone>123-4567</homePhone>\n" +
            "    <gender>M</gender>\n" +
            "  </modifiedBy>\n" +
            "  <modifiedDateTime>2016-11-15T11:38:22.496</modifiedDateTime>\n" +
            "  <orderedDateTime>2016-11-15T11:19:43.529</orderedDateTime>\n" +
            "  <processedDateTime>2016-11-15T11:20:08.96</processedDateTime>\n" +
            "  <completedDateTime>2016-11-15T11:38:18.498</completedDateTime>\n" +
            "  <approvedDateTime>2016-11-15T11:38:22.45</approvedDateTime>\n" +
            "  <rejectedDateTime/>\n" +
            "  <lastSfmsSyncDateTime/>\n" +
            "  <savedInSfms>false</savedInSfms>\n" +
            "</RequisitionView>";
}
