package gov.nysenate.ess.supply.unit.synchronization;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.service.notification.slack.service.SlackChatService;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.supply.InMemoryRequisitionDao;
import gov.nysenate.ess.supply.InMemorySyncAttemptDao;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.model.Category;
import gov.nysenate.ess.supply.item.model.ItemAllowance;
import gov.nysenate.ess.supply.item.model.ItemStatus;
import gov.nysenate.ess.supply.item.model.ItemUnit;
import gov.nysenate.ess.supply.item.model.SupplyItem;
import gov.nysenate.ess.supply.notification.SupplyEmailService;
import gov.nysenate.ess.supply.requisition.model.*;
import gov.nysenate.ess.supply.requisition.service.RequisitionService;
import gov.nysenate.ess.supply.requisition.service.SupplyRequisitionService;
import gov.nysenate.ess.supply.requisition.view.RequisitionView;
import gov.nysenate.ess.supply.requisition.view.SfmsRequisitionView;
import gov.nysenate.ess.supply.synchronization.SyncStatuses.ModifySyncStatus;
import gov.nysenate.ess.supply.synchronization.dao.RequisitionSyncAttemptDao;
import gov.nysenate.ess.supply.synchronization.dao.SfmsSynchronizationProcedure;
import gov.nysenate.ess.supply.synchronization.model.RequisitionSyncAttempt;
import gov.nysenate.ess.supply.synchronization.service.SfmsSynchronizationService;
import gov.nysenate.ess.supply.util.date.DateTimeFactory;
import gov.nysenate.ess.supply.util.date.DummyDateTime;
import org.apache.shiro.dao.DataAccessException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataAccessResourceFailureException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@org.junit.experimental.categories.Category(UnitTest.class)
public class SfmsSynchronizationServiceTest {

    @Mock
    private SfmsSynchronizationProcedure synchronizationProcedure;
    @Mock
    private SlackChatService slackChatService;
    @Mock
    private SupplyEmailService emailService;

    private DummyDateTime dummyDateTime;
    private RequisitionService requisitionService;
    private SfmsSynchronizationService service;


    private final InMemorySyncAttemptDao inMemorySyncAttemptDao = new InMemorySyncAttemptDao();


    @Before
    public void setup() {
        requisitionService = new SupplyRequisitionService(new InMemoryRequisitionDao(), emailService);
        dummyDateTime = new DummyDateTime();
        service = new SfmsSynchronizationService(
                true,
                requisitionService,
                synchronizationProcedure,
                dummyDateTime,
                slackChatService,
                inMemorySyncAttemptDao
        );

    }

    @Test
    public void givenSyncDisabled_thenSkipSync() {
        service = new SfmsSynchronizationService(
                false,
                requisitionService,
                synchronizationProcedure,
                dummyDateTime,
                slackChatService,
                inMemorySyncAttemptDao
        );
        service.synchronizeRequisitions();
        verifyNoInteractions(synchronizationProcedure, slackChatService);
    }

    public RequisitionSyncAttempt fillRequisitionSyncAttempt(Requisition requisition) {
        RequisitionSyncAttempt syncAttempt = new RequisitionSyncAttempt();

        if (!requisition.getSfmsSyncStatus().equals(SyncStatus.ERROR)) {
            syncAttempt.setWasSuccessful(true);
        } else {
            syncAttempt.setWasSuccessful(false);
        }
        syncAttempt.setRequisitionId(requisition.getRequisitionId());
        syncAttempt.setSyncAttempts(requisition.getSfmsSyncAttempts());
        syncAttempt.setOutcomeSyncStatus(requisition.getSfmsSyncStatus());
        syncAttempt.setAttemptSyncDate(LocalDateTime.now());
        List<Integer> itemIds = new ArrayList<>();
        for (LineItem lineItem : requisition.getLineItems()) {
            itemIds.add(lineItem.getItem().getId());
        }
        syncAttempt.setSyncableLineItems(itemIds);
        return syncAttempt;
    }


    @Test
    public void testSuccessfulSync() {
        // Initialize test state.
        Requisition requisition = buildRequisition(1001, setOf(lineItem(1, true)));

        LocalDateTime expectedSyncDateTime = LocalDateTime.now();
        dummyDateTime.setDateTime(expectedSyncDateTime);

        requisition = requisition.setLastSfmsSyncDateTimeDateTime(expectedSyncDateTime);

        requisitionService.saveRequisition(requisition);


        // Execute method to test
        service.synchronizeRequisitions();

        // Fetch state after execution.
        requisition = requisitionService.getRequisitionById(requisition.getRequisitionId()).get();

        // Check for valid side effects.
        assertTrue(requisition.getSavedInSfms());
        assertEquals(expectedSyncDateTime, requisition.getLastSfmsSyncDateTime().get());
        assertEquals(SyncStatus.COMPLETE, requisition.getSfmsSyncStatus());
        assertNull(requisition.getSfmsSkippedReason());
        assertEquals(1, requisition.getSfmsSyncAttempts());

        RequisitionSyncAttempt syncAttempt = fillRequisitionSyncAttempt(requisition);


        assertEquals(SyncStatus.COMPLETE, syncAttempt.getOutcomeSyncStatus());
        assertEquals(syncAttempt.getSyncAttempts(), requisition.getSfmsSyncAttempts());
        assertTrue(syncAttempt.getWasSuccessful());
        assertNull(syncAttempt.getErrorInfo());
        assertEquals(1, syncAttempt.getSyncableLineItems().size());
        assertEquals(1, inMemorySyncAttemptDao.getSyncAttemptsByReqId(requisition.getRequisitionId()).size());
    }

    @Test
    public void testRejectedSync() {
        // Initialize test state.
        Requisition requisition = buildRejectedRequisition(1002, setOf(lineItem(1, false)));

        LocalDateTime expectedSyncDateTime = LocalDateTime.now();
        dummyDateTime.setDateTime(expectedSyncDateTime);

        requisition = requisition.setLastSfmsSyncDateTimeDateTime(expectedSyncDateTime);

        requisitionService.saveRequisition(requisition);
        //System.out.println(requisition);

        // Execute method to test
        service.synchronizeRequisitions();

        requisition = requisitionService.getRequisitionById(requisition.getRequisitionId()).get();

        assertEquals(SyncStatus.SKIPPED, requisition.getSfmsSyncStatus());
        assertEquals(SkippedReason.REJECTED, requisition.getSfmsSkippedReason());
        assertNotNull(requisition.getRejectedDateTime());
        assertNotNull(requisition.getSfmsSkippedReason());
        assertEquals(0, requisition.getSfmsSyncAttempts());

        RequisitionSyncAttempt syncAttempt = fillRequisitionSyncAttempt(requisition);

        assertEquals(SyncStatus.SKIPPED, syncAttempt.getOutcomeSyncStatus());
        assertTrue(syncAttempt.getWasSuccessful());
        assertNull(syncAttempt.getErrorInfo());
        assertEquals(1, syncAttempt.getSyncableLineItems().size());
        assertEquals(1, inMemorySyncAttemptDao.getSyncAttemptsByReqId(requisition.getRequisitionId()).size());
        assertEquals(syncAttempt.getSyncAttempts(), requisition.getSfmsSyncAttempts());
        assertNotNull(syncAttempt.getAttemptSyncDate());
    }


    @Test // I remember you said this was an edge case
    public void test_No_Syncable_Items_And_Rejected_Sync() {
        // Initialize test state.
        Requisition requisition = buildRejectedRequisition(1003, setOf(lineItem(1, false)));

        LocalDateTime expectedSyncDateTime = LocalDateTime.now();
        dummyDateTime.setDateTime(expectedSyncDateTime);

        requisitionService.saveRequisition(requisition);

        // Execute method to test
        service.synchronizeRequisitions();

        requisition = requisitionService.getRequisitionById(requisition.getRequisitionId()).get();

        assertEquals(SyncStatus.SKIPPED, requisition.getSfmsSyncStatus());
        assertNotNull(requisition.getSfmsSkippedReason());
        assertEquals(SkippedReason.REJECTED, requisition.getSfmsSkippedReason());
        assertNotNull(requisition.getRejectedDateTime());
        assertEquals(0, requisition.getSfmsSyncAttempts());

        RequisitionSyncAttempt syncAttempt = fillRequisitionSyncAttempt(requisition);
        assertEquals(SyncStatus.SKIPPED, syncAttempt.getOutcomeSyncStatus());
        assertTrue(syncAttempt.getWasSuccessful());
        assertNull(syncAttempt.getErrorInfo());
        assertEquals(1, syncAttempt.getSyncableLineItems().size());
        assertEquals(syncAttempt.getSyncAttempts(), requisition.getSfmsSyncAttempts());
        assertNotNull(syncAttempt.getAttemptSyncDate());

    }

    @Test
    public void test_No_Syncable_Items_Sync() {
        // Initialize test state.
        Requisition requisition = buildRequisition(1004, setOf(lineItem(1, false), lineItem(2, false)));

        LocalDateTime expectedSyncDateTime = LocalDateTime.now();
        dummyDateTime.setDateTime(expectedSyncDateTime);

        requisitionService.saveRequisition(requisition);

        // Execute method to test
        service.synchronizeRequisitions();

        requisition = requisitionService.getRequisitionById(requisition.getRequisitionId()).get();

        assertEquals(SyncStatus.SKIPPED, requisition.getSfmsSyncStatus());
        assertNotNull(requisition.getSfmsSkippedReason());
        assertEquals(SkippedReason.NO_SYNCABLE_ITEMS, requisition.getSfmsSkippedReason());
        assertEquals(0, requisition.getSfmsSyncAttempts());

        RequisitionSyncAttempt syncAttempt = fillRequisitionSyncAttempt(requisition);
        assertEquals(SyncStatus.SKIPPED, syncAttempt.getOutcomeSyncStatus());
        assertTrue(syncAttempt.getWasSuccessful());
        assertNull(syncAttempt.getErrorInfo());
        assertEquals(syncAttempt.getSyncAttempts(), requisition.getSfmsSyncAttempts());
        assertEquals(2, syncAttempt.getSyncableLineItems().size());
        assertNotNull(syncAttempt.getAttemptSyncDate());
        assertEquals(syncAttempt.getSyncAttempts(), requisition.getSfmsSyncAttempts());
    }

    @Test // Can't test since I don't know how to exactly
    public void testErrorSync() {
        // Initialize test state.
        Requisition requisition = buildRequisition(1005, setOf(lineItem(1, true)));

        requisitionService.saveRequisition(requisition);

        LocalDateTime expectedSyncDateTime = LocalDateTime.now();
        dummyDateTime.setDateTime(expectedSyncDateTime);

        // Execute method to test
        service.synchronizeRequisitions();

        // Fetch state after execution.
        requisition = requisitionService.getRequisitionById(requisition.getRequisitionId()).get();

        // Check for valid side effects.
        assertFalse(requisition.getSavedInSfms());
        assertEquals(SyncStatus.ERROR, requisition.getSfmsSyncStatus());
        assertNull(requisition.getSfmsSkippedReason());
        assertEquals(1, requisition.getSfmsSyncAttempts());

        List<RequisitionSyncAttempt> syncAttempt = inMemorySyncAttemptDao.getSyncAttemptsByReqId(requisition.getRequisitionId());

        assertEquals(SyncStatus.ERROR, syncAttempt.get(0).getOutcomeSyncStatus());
        assertFalse(syncAttempt.isEmpty());
        assertFalse(syncAttempt.get(0).getWasSuccessful());
        assertNotNull(syncAttempt.get(0).getErrorInfo());
        assertEquals(1, syncAttempt.get(0).getSyncableLineItems().size());
        assertEquals(1, syncAttempt.get(0).getSyncAttempts());
        assertEquals(requisition.getLineItems().size(), syncAttempt.get(0).getSyncableLineItems().size());
        assertNotNull(syncAttempt.get(0).getAttemptSyncDate());
    }

    @Test // Can't test since I don't know how to exactly
    public void testMultipleErrorSync() {
        // Initialize test state.
        Requisition requisition = buildRequisition(1006, setOf(lineItem(1, true)));

        requisitionService.saveRequisition(requisition);

        LocalDateTime expectedSyncDateTime = LocalDateTime.now();
        dummyDateTime.setDateTime(expectedSyncDateTime);

        // Execute method to test
        service.synchronizeRequisitions();
        service.synchronizeRequisitions();
        service.synchronizeRequisitions();
        service.synchronizeRequisitions();


        // Fetch state after execution.
        requisition = requisitionService.getRequisitionById(requisition.getRequisitionId()).get();

        // Check for valid side effects.
        assertFalse(requisition.getSavedInSfms());
        assertEquals(SyncStatus.ERROR, requisition.getSfmsSyncStatus());
        assertNull(requisition.getSfmsSkippedReason());
        assertEquals(4, requisition.getSfmsSyncAttempts());

        List<RequisitionSyncAttempt> syncAttempt = inMemorySyncAttemptDao.getSyncAttemptsByReqId(requisition.getRequisitionId());

        assertEquals(SyncStatus.ERROR, syncAttempt.get(0).getOutcomeSyncStatus());
        assertFalse(syncAttempt.isEmpty());
        assertFalse(syncAttempt.get(0).getWasSuccessful());
        assertFalse(syncAttempt.get(1).getWasSuccessful());
        assertFalse(syncAttempt.get(2).getWasSuccessful());
        assertFalse(syncAttempt.get(3).getWasSuccessful());

        assertNotNull(syncAttempt.get(0).getErrorInfo());
        assertNotNull(syncAttempt.get(1).getErrorInfo());
        assertNotNull(syncAttempt.get(2).getErrorInfo());
        assertNotNull(syncAttempt.get(3).getErrorInfo());

        assertEquals(1, syncAttempt.get(0).getSyncAttempts());
        assertEquals(2, syncAttempt.get(1).getSyncAttempts());
        assertEquals(3, syncAttempt.get(2).getSyncAttempts());
        assertEquals(4, syncAttempt.get(3).getSyncAttempts());

        assertEquals(requisition.getRequisitionId(), syncAttempt.get(0).getRequisitionId());
        assertEquals(requisition.getRequisitionId(), syncAttempt.get(1).getRequisitionId());
        assertEquals(requisition.getRequisitionId(), syncAttempt.get(2).getRequisitionId());
        assertEquals(requisition.getRequisitionId(), syncAttempt.get(3).getRequisitionId());

        assertEquals(4, syncAttempt.size());
    }

    @Test // Can't test since I don't know how to exactly
    public void testMultipleErrorSyncThenSucceed() {
        // Initialize test state.
        Requisition requisition = buildRequisition(1007, setOf(lineItem(1, true)));

        requisitionService.saveRequisition(requisition);

        LocalDateTime expectedSyncDateTime = LocalDateTime.now();
        dummyDateTime.setDateTime(expectedSyncDateTime);

        // Execute method to test
        service.synchronizeRequisitions();
        service.synchronizeRequisitions();
        service.synchronizeRequisitions();
        service.synchronizeRequisitions();
        service.synchronizeRequisitions();


        // Fetch state after execution.
        requisition = requisitionService.getRequisitionById(requisition.getRequisitionId()).get();

        // Check for valid side effects.
        assertTrue(requisition.getSavedInSfms());
        assertEquals(SyncStatus.COMPLETE, requisition.getSfmsSyncStatus());
        assertNull(requisition.getSfmsSkippedReason());
        assertEquals(5, requisition.getSfmsSyncAttempts());

        List<RequisitionSyncAttempt> syncAttempt = inMemorySyncAttemptDao.getSyncAttemptsByReqId(requisition.getRequisitionId());

        assertEquals(SyncStatus.COMPLETE, syncAttempt.get(4).getOutcomeSyncStatus());
        assertFalse(syncAttempt.isEmpty());
        assertFalse(syncAttempt.get(0).getWasSuccessful());
        assertFalse(syncAttempt.get(1).getWasSuccessful());
        assertFalse(syncAttempt.get(2).getWasSuccessful());
        assertFalse(syncAttempt.get(3).getWasSuccessful());
        assertTrue(syncAttempt.get(4).getWasSuccessful());


        assertNotNull(syncAttempt.get(0).getErrorInfo());
        assertNotNull(syncAttempt.get(1).getErrorInfo());
        assertNotNull(syncAttempt.get(2).getErrorInfo());
        assertNotNull(syncAttempt.get(3).getErrorInfo());
        assertNull(syncAttempt.get(4).getErrorInfo());


        assertEquals(1, syncAttempt.get(0).getSyncAttempts());
        assertEquals(2, syncAttempt.get(1).getSyncAttempts());
        assertEquals(3, syncAttempt.get(2).getSyncAttempts());
        assertEquals(4, syncAttempt.get(3).getSyncAttempts());
        assertEquals(5, syncAttempt.get(4).getSyncAttempts());


        assertEquals(requisition.getRequisitionId(), syncAttempt.get(0).getRequisitionId());
        assertEquals(requisition.getRequisitionId(), syncAttempt.get(1).getRequisitionId());
        assertEquals(requisition.getRequisitionId(), syncAttempt.get(2).getRequisitionId());
        assertEquals(requisition.getRequisitionId(), syncAttempt.get(3).getRequisitionId());
        assertEquals(requisition.getRequisitionId(), syncAttempt.get(4).getRequisitionId());

        assertEquals(5, syncAttempt.size());
    }


    private Requisition buildRequisition(int requisitionId, Set<LineItem> lineItems) {
        Employee customer = new Employee();
        customer.setEmployeeId(10);

        Employee issuer = new Employee();
        issuer.setEmployeeId(20);
        issuer.setUid("issuer" + requisitionId);

        Location destination = new Location(new LocationId("A42FB", 'W'));
        LocalDateTime now = LocalDateTime.of(2020, 1, 1, 0, 0);

        return new Requisition.Builder()
                .withRequisitionId(requisitionId)
                .withRevisionId(1)
                .withCustomer(customer)
                .withDestination(destination)
                .withDeliveryMethod(DeliveryMethod.DELIVERY)
                .withLineItems(lineItems)
                .withState(new ApprovedState())
                .withIssuer(issuer)
                .withModifiedBy(customer)
                .withModifiedDateTime(now)
                .withOrderedDateTime(now)
                .withProcessedDateTime(LocalDateTime.now())
                .withCompletedDateTime(LocalDateTime.now())
                .withApprovedDateTime(LocalDateTime.now())
                .build();
    }

    private Requisition buildRejectedRequisition(int requisitionId, Set<LineItem> lineItems) {
        Employee customer = new Employee();
        customer.setEmployeeId(11);

        Employee issuer = new Employee();
        issuer.setEmployeeId(21);
        issuer.setUid("issuer" + requisitionId);

        Location destination = new Location(new LocationId("A42FB", 'W'));
        LocalDateTime now = LocalDateTime.of(2020, 1, 1, 0, 0);

        return new Requisition.Builder()
                .withRequisitionId(requisitionId)
                .withRevisionId(1)
                .withCustomer(customer)
                .withDestination(destination)
                .withDeliveryMethod(DeliveryMethod.DELIVERY)
                .withLineItems(lineItems)
                .withState(new RejectedState())
                .withIssuer(issuer)
                .withModifiedBy(customer)
                .withModifiedDateTime(now)
                .withOrderedDateTime(now)
                .withProcessedDateTime(LocalDateTime.now())
                .withCompletedDateTime(LocalDateTime.now())
                .withRejectedDateTime(LocalDateTime.now())
                .build();
    }

    private static LineItem lineItem(int quantity, boolean requiresSync) {
        ItemStatus status = requiresSync
                ? new ItemStatus(true, false, true, false)
                : new ItemStatus(true, true, true, false);
        SupplyItem item = new SupplyItem.Builder()
                .withId(requiresSync ? 1 : 2)
                .withCommodityCode("X")
                .withDescription("Test Item")
                .withStatus(status)
                .withCategory(new Category("Test"))
                .withAllowance(new ItemAllowance(10, 100))
                .withUnit(new ItemUnit("EA", 1))
                .withReconciliationPage(1)
                .build();
        return new LineItem(item, quantity);
    }

    private static Set<LineItem> setOf(LineItem... items) {
        Set<LineItem> set = new HashSet<>();
        Collections.addAll(set, items);
        return set;
    }
}
