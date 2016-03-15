package gov.nysenate.ess.supply.unit.shipment;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.supply.shipment.Shipment;
import gov.nysenate.ess.supply.shipment.ShipmentHistory;
import gov.nysenate.ess.supply.shipment.ShipmentStatus;
import gov.nysenate.ess.supply.shipment.ShipmentVersion;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class ShipmentTests {

    private Shipment pendingShipment;
    private ShipmentVersion pendingVersion;
    private LocalDateTime pendingDateTime;
    private LocalDateTime processDateTime;
    private LocalDateTime completedDateTime;
    private Employee issuingEmployee;
    private Employee procesedByEmp;
    private Employee completedByEmp;

    @Before
    public void setup() {
        pendingDateTime = LocalDateTime.now();
        pendingVersion = new ShipmentVersion.Builder().withId(1).withStatus(ShipmentStatus.PENDING).build();
        pendingShipment = Shipment.of(1, null, ShipmentHistory.of(pendingDateTime, pendingVersion));

        processDateTime = pendingDateTime.plusMinutes(10);
        completedDateTime = processDateTime.plusMinutes(10);
        issuingEmployee = new Employee();
        issuingEmployee.setEmployeeId(1);
        procesedByEmp = new Employee();
        procesedByEmp.setEmployeeId(2);
        completedByEmp = new Employee();
        completedByEmp.setEmployeeId(3);
    }

    @Test
    public void canProcessShipment() {
        Shipment processed = pendingShipment.process(issuingEmployee, procesedByEmp, processDateTime);
        assertEquals(issuingEmployee, processed.getIssuingEmployee());
        assertEquals(procesedByEmp, processed.getModifiedBy());
        assertEquals(ShipmentStatus.PROCESSING, processed.getStatus());
        assertEquals(processDateTime, processed.getProcessedDateTime().get());
    }

    // can only process pending shipment.

    @Test
    public void canCompleteShipment() {
        Shipment processed = pendingShipment.process(issuingEmployee, procesedByEmp, processDateTime);
        Shipment completed = processed.complete(completedByEmp, completedDateTime);

        assertEquals(completedByEmp, completed.getModifiedBy());
        assertEquals(completedDateTime, completed.getCompletedDateTime().get());
        assertEquals(ShipmentStatus.COMPLETED, completed.getStatus());
    }

    // can only complete processing shipment

    @Test
    public void canSubmitToSfms() {
        LocalDateTime submittedDateTime = completedDateTime.plusMinutes(10);
        Employee submittedEmp = new Employee();
        submittedEmp.setEmployeeId(20);
        Shipment processed = pendingShipment.process(issuingEmployee, procesedByEmp, processDateTime);
        Shipment completed = processed.complete(completedByEmp, completedDateTime);

        Shipment submitted = completed.submitToSfms(submittedEmp, submittedDateTime);

        assertEquals(submittedEmp, submitted.getModifiedBy());
        assertEquals(submittedDateTime, submitted.getApprovedDateTime().get());
        assertEquals(ShipmentStatus.APPROVED, submitted.getStatus());
    }

    // can only submit completed shipment.

    @Test
    public void canCancelShipment() {
        LocalDateTime canceledDateTime = completedDateTime.plusMinutes(10);
        Employee canceledEmp = new Employee();
        canceledEmp.setEmployeeId(20);
        Shipment processed = pendingShipment.process(issuingEmployee, procesedByEmp, processDateTime);
        Shipment completed = processed.complete(completedByEmp, completedDateTime);

        Shipment canceled = completed.cancel(canceledEmp, canceledDateTime);

        assertEquals(canceledEmp, canceled.getModifiedBy());
        assertEquals(ShipmentStatus.CANCELED, canceled.getStatus());
        assertEquals(canceledDateTime, canceled.getCanceledDateTime().get());
    }

    @Test
    public void canUpdateIssuingEmp() {
        LocalDateTime updatedDateTime = completedDateTime.plusMinutes(10);
        Employee updatedIssuer = new Employee();
        updatedIssuer.setEmployeeId(20);
        Employee modifiedBy = new Employee();
        modifiedBy.setEmployeeId(21);
        Shipment processed = pendingShipment.process(issuingEmployee, procesedByEmp, processDateTime);

        Shipment shipment = processed.updateIssuingEmployee(updatedIssuer, modifiedBy, updatedDateTime);
        assertEquals(updatedIssuer, shipment.getIssuingEmployee());
    }

    @Test
    public void processedDateTimeIsCorrectAfterCompleting() {
        Shipment processed = pendingShipment.process(issuingEmployee, procesedByEmp, processDateTime);
        Shipment completed = processed.complete(completedByEmp, completedDateTime);
        assertEquals(processDateTime, completed.getProcessedDateTime().get());
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
