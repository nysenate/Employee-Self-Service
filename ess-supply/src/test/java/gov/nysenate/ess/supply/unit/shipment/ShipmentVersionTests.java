package gov.nysenate.ess.supply.unit.shipment;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.supply.SupplyUnitTests;
import gov.nysenate.ess.supply.shipment.ShipmentStatus;
import gov.nysenate.ess.supply.shipment.ShipmentVersion;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class ShipmentVersionTests extends SupplyUnitTests {

    private static Employee stubEmployee;

    @BeforeClass
    public static void setupStubs() {
        stubEmployee = new Employee();
        stubEmployee.setEmployeeId(1);
    }

    @Test
    public void testInitializationRequirements() {
        assertIdNotNegative();
        assertStatusNotNull();
        assertModifiedByNotNull();
        assertIssuerNotNullWhenStatus(ShipmentStatus.PROCESSING);
        assertIssuerNotNullWhenStatus(ShipmentStatus.COMPLETED);
        assertIssuerNotNullWhenStatus(ShipmentStatus.APPROVED);
    }

    private void assertIdNotNegative() {
        try {
            new ShipmentVersion.Builder().withId(-1).withModifiedBy(stubEmployee).withStatus(ShipmentStatus.PENDING).build();
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    private void assertStatusNotNull() {
        try {
            new ShipmentVersion.Builder().withId(1).withModifiedBy(stubEmployee).withStatus(null).build();
            fail();
        } catch (NullPointerException e) {
        }
    }

    private void assertModifiedByNotNull() {
        try {
            new ShipmentVersion.Builder().withId(1).withModifiedBy(null).withStatus(ShipmentStatus.PENDING).build();
            fail();
        } catch (NullPointerException e) {
        }
    }

    private void assertIssuerNotNullWhenStatus(ShipmentStatus status) {
        try {
            new ShipmentVersion.Builder().withId(0).withModifiedBy(stubEmployee).withStatus(status).build();
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testDefaultValues() {
        ShipmentVersion version = new ShipmentVersion.Builder().withModifiedBy(stubEmployee)
                                                               .withStatus(ShipmentStatus.PENDING).build();
        assertTrue(version.getId() == 0);
        assertIssuingEmployeeIsNull(version);
    }

    private void assertIssuingEmployeeIsNull(ShipmentVersion version) {
        assertTrue(!version.getIssuingEmployee().isPresent());
    }
}
