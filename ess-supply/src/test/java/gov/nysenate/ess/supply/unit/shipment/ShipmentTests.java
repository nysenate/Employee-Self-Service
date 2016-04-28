package gov.nysenate.ess.supply.unit.shipment;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.supply.SupplyUnitTests;
import gov.nysenate.ess.supply.shipment.ShipmentStatus;
import gov.nysenate.ess.supply.shipment.ShipmentVersion;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ShipmentTests extends SupplyUnitTests {

    private static Location stubLocation;
    private static Employee stubEmployee;

    @BeforeClass
    public static void setupClass() {
        stubLocation = new Location(new LocationId("A42FB", 'W'));
        stubEmployee = new Employee();
        stubEmployee.setEmployeeId(1);
    }

    @Test(expected = NullPointerException.class)
    public void shipmentVersionRequiresNonNullStatus() {
        ShipmentVersion version = new ShipmentVersion.Builder().withId(1).withModifiedBy(stubEmployee)
                                                                       .withStatus(null).build();
    }

    @Test(expected = NullPointerException.class)
    public void shipmentVersionRequiresNonNullModifiedByEmployee() {
        ShipmentVersion version = new ShipmentVersion.Builder().withId(1).withModifiedBy(null)
                                                                       .withStatus(ShipmentStatus.PENDING).build();
    }

    public class WithShipmentVersion {

        private ShipmentVersion shipmentVersion;

        @Before
        public void createShipmentVersion() {
            shipmentVersion = new ShipmentVersion.Builder().withId(1).withModifiedBy(stubEmployee)
                                                           .withStatus(ShipmentStatus.PENDING).build();
        }

        @Test
        public void checkShipmentVersionDefaultValues() {
            ShipmentVersion version = new ShipmentVersion.Builder().withModifiedBy(stubEmployee)
                                                                   .withStatus(ShipmentStatus.PENDING).build();
            assertTrue(version.getId() == 0);
            assertIssuingEmployeeIsNull(version);
        }

        private void assertIssuingEmployeeIsNull(ShipmentVersion version) {
            assertTrue(!version.getIssuingEmployee().isPresent());
        }

    }
}
