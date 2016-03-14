package gov.nysenate.ess.supply.unit.shipment;

import gov.nysenate.ess.supply.shipment.Shipment;
import gov.nysenate.ess.supply.shipment.ShipmentHistory;
import gov.nysenate.ess.supply.shipment.ShipmentStatus;
import gov.nysenate.ess.supply.shipment.ShipmentVersion;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class ShipmentTests {

    @Test
    public void correctlyGetsProcessedDateTime() {
        LocalDateTime pendingTime = LocalDateTime.now();
        ShipmentVersion pendingVersion = new ShipmentVersion.Builder().withId(1).withStatus(ShipmentStatus.PENDING).build();
        LocalDateTime processedTime = pendingTime.plusMinutes(15);
        ShipmentVersion processedVersion = new ShipmentVersion.Builder().withId(2).withStatus(ShipmentStatus.PROCESSING).build();
        LocalDateTime completedTime = processedTime.plusMinutes(32);
        ShipmentVersion completedVersion = new ShipmentVersion.Builder().withId(3).withStatus(ShipmentStatus.COMPLETED).build();
        SortedMap<LocalDateTime, ShipmentVersion> versions = new TreeMap<>();
        versions.put(pendingTime, pendingVersion);
        versions.put(processedTime, processedVersion);
        versions.put(completedTime, completedVersion);

        Shipment shipment = Shipment.of(1, null, ShipmentHistory.of(versions));
        assertEquals(processedTime, shipment.getProcessedDateTime().get());
    }

    // TODO: Waiting on implementing this. May become more clear when we get feedback on what needs to be 'undo' able.
    @Ignore
    @Test
    public void getsMostRecentCompletedDateTimeIfMultiple() {
        LocalDateTime pendingTime = LocalDateTime.now();
        ShipmentVersion pendingVersion = new ShipmentVersion.Builder().withId(1).withStatus(ShipmentStatus.PENDING).build();
        LocalDateTime processedTime = pendingTime.plusMinutes(15);
        ShipmentVersion processedVersion = new ShipmentVersion.Builder().withId(2).withStatus(ShipmentStatus.PROCESSING).build();
        LocalDateTime completedTime = processedTime.plusMinutes(15);
        ShipmentVersion completedVersion = new ShipmentVersion.Builder().withId(3).withStatus(ShipmentStatus.COMPLETED).build();
        LocalDateTime undoCompletedTime = completedTime.plusMinutes(15);
        ShipmentVersion undoComplete = new ShipmentVersion.Builder().withId(4).withStatus(ShipmentStatus.PROCESSING).build();
        LocalDateTime completeAgain = undoCompletedTime.plusMinutes(15);
        ShipmentVersion completeAgainVersion = new ShipmentVersion.Builder().withId(5).withStatus(ShipmentStatus.COMPLETED).build();

        SortedMap<LocalDateTime, ShipmentVersion> versions = new TreeMap<>();
        versions.put(pendingTime, pendingVersion);
        versions.put(processedTime, processedVersion);
        versions.put(completedTime, completedVersion);
        versions.put(undoCompletedTime, undoComplete);
        versions.put(completeAgain, completeAgainVersion);

        Shipment shipment = Shipment.of(1, null, ShipmentHistory.of(versions));
        assertEquals(completeAgain, shipment.getCompletedDateTime().get());
    }
}
