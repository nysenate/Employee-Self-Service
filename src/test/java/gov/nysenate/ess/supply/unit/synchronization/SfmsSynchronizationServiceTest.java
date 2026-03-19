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
import gov.nysenate.ess.supply.synchronization.model.RequisitionSyncResult;
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


        RequisitionSyncResult result = service.syncRequisition(requisition);


        // Check for valid side effects.
        assertEquals(expectedSyncDateTime, result.getUpdatedRequisition().getLastSfmsSyncDateTime().get());
        assertEquals(SyncStatus.COMPLETE, result.getUpdatedRequisition().getSfmsSyncStatus());
        assertNull(result.getUpdatedRequisition().getSfmsSkippedReason());
        assertEquals(1, result.getUpdatedRequisition().getSfmsSyncAttempts());


        assertEquals(SyncStatus.COMPLETE, result.getSyncAttempt().getOutcomeSyncStatus());
        assertEquals(1, result.getSyncAttempt().getSyncAttempts());
        assertTrue(result.getSyncAttempt().getWasSuccessful());
        assertNull(result.getSyncAttempt().getErrorInfo());
        assertEquals(1, result.getSyncAttempt().getSyncableLineItems().size());
        assertEquals(1, inMemorySyncAttemptDao.getSyncAttemptsByReqId(requisition.getRequisitionId()).size());
    }

    @Test
    public void testSucessfulSyncWithANonSyncableItem() {
        Requisition requisition = buildRequisition(1001, setOf(lineItem(1, true), lineItem(2, false)));

        LocalDateTime expectedSyncDateTime = LocalDateTime.now();
        dummyDateTime.setDateTime(expectedSyncDateTime);

        // Execute method to test
        RequisitionSyncResult result = service.syncRequisition(requisition);

        // Check for valid side effects.
        assertEquals(expectedSyncDateTime, result.getUpdatedRequisition().getLastSfmsSyncDateTime().get());
        assertEquals(SyncStatus.COMPLETE, result.getUpdatedRequisition().getSfmsSyncStatus());
        assertNull(result.getUpdatedRequisition().getSfmsSkippedReason());
        assertEquals(1, result.getUpdatedRequisition().getSfmsSyncAttempts());

        assertEquals(1001, result.getSyncAttempt().getRequisitionId());
        assertEquals(1, result.getSyncAttempt().getSyncAttempts());
        assertEquals(expectedSyncDateTime, result.getSyncAttempt().getAttemptSyncDate());
        assertTrue(result.getSyncAttempt().getWasSuccessful());
        assertNull(result.getSyncAttempt().getErrorInfo()); // you decide if error info should be null or an empty string when there was no error then enforce consistently
        assertEquals(SyncStatus.COMPLETE, result.getSyncAttempt().getOutcomeSyncStatus());
        assertEquals(new ArrayList<>(List.of(1)), result.getSyncAttempt().getSyncableLineItems());
    }

    @Test
    public void testRejectedSync() {
        // Initialize test state.
        Requisition requisition = buildRejectedRequisition(1002, setOf(lineItem(1, false)));

        LocalDateTime expectedSyncDateTime = LocalDateTime.now();
        dummyDateTime.setDateTime(expectedSyncDateTime);

        requisition = requisition.setLastSfmsSyncDateTimeDateTime(expectedSyncDateTime);

        RequisitionSyncResult result = service.syncRequisition(requisition);

        assertEquals(expectedSyncDateTime, result.getUpdatedRequisition().getLastSfmsSyncDateTime().get());
        assertEquals(SyncStatus.SKIPPED, result.getUpdatedRequisition().getSfmsSyncStatus());
        assertEquals(SkippedReason.REJECTED, result.getUpdatedRequisition().getSfmsSkippedReason());
        assertEquals(0, result.getUpdatedRequisition().getSfmsSyncAttempts());


        assertEquals(SyncStatus.SKIPPED, result.getSyncAttempt().getOutcomeSyncStatus());
        assertFalse(result.getSyncAttempt().getWasSuccessful());
        assertNull(result.getSyncAttempt().getErrorInfo());
        assertEquals(0, result.getSyncAttempt().getSyncableLineItems().size());
        assertEquals(result.getSyncAttempt().getSyncAttempts(), requisition.getSfmsSyncAttempts());
        assertNotNull(result.getSyncAttempt().getAttemptSyncDate());
    }


    @Test // I remember you said this was an edge case
    public void test_No_Syncable_Items_And_Rejected_Sync() {
        // Initialize test state.
        Requisition requisition = buildRejectedRequisition(1003, setOf(lineItem(1, false)));

        LocalDateTime expectedSyncDateTime = LocalDateTime.now();
        dummyDateTime.setDateTime(expectedSyncDateTime);


        //requisitionService.saveRequisition(requisition);

        // Execute method to test
        RequisitionSyncResult result = service.syncRequisition(requisition);


        assertEquals(expectedSyncDateTime, result.getUpdatedRequisition().getLastSfmsSyncDateTime().get());
        assertEquals(SyncStatus.SKIPPED, result.getUpdatedRequisition().getSfmsSyncStatus());
        assertNotNull(result.getUpdatedRequisition().getSfmsSkippedReason());
        assertEquals(SkippedReason.REJECTED, result.getUpdatedRequisition().getSfmsSkippedReason());
        assertEquals(0, result.getUpdatedRequisition().getSfmsSyncAttempts());

        assertEquals(SyncStatus.SKIPPED, result.getSyncAttempt().getOutcomeSyncStatus());
        assertFalse(result.getSyncAttempt().getWasSuccessful());
        assertNull(result.getSyncAttempt().getErrorInfo());
        assertEquals(0, result.getSyncAttempt().getSyncableLineItems().size());
        assertNotNull(result.getSyncAttempt().getAttemptSyncDate());
    }

    @Test
    public void test_No_Syncable_Items_Sync() {
        // Initialize test state.
        Requisition requisition = buildRequisition(1004, setOf(lineItem(1, false), lineItem(2, false)));

        LocalDateTime expectedSyncDateTime = LocalDateTime.now();
        dummyDateTime.setDateTime(expectedSyncDateTime);


        // Execute method to test
        RequisitionSyncResult result = service.syncRequisition(requisition);

        assertEquals(expectedSyncDateTime, result.getUpdatedRequisition().getLastSfmsSyncDateTime().get());
        assertEquals(SyncStatus.SKIPPED, result.getUpdatedRequisition().getSfmsSyncStatus());
        assertNotNull(result.getUpdatedRequisition().getSfmsSkippedReason());
        assertEquals(SkippedReason.NO_SYNCABLE_ITEMS, result.getUpdatedRequisition().getSfmsSkippedReason());
        assertNull(result.getSyncAttempt().getErrorInfo());
        assertEquals(0, result.getUpdatedRequisition().getSfmsSyncAttempts());


        assertEquals(SyncStatus.SKIPPED, result.getSyncAttempt().getOutcomeSyncStatus());
        assertFalse(result.getSyncAttempt().getWasSuccessful());
        assertNull(result.getSyncAttempt().getErrorInfo());
        assertEquals(result.getSyncAttempt().getSyncAttempts(), requisition.getSfmsSyncAttempts());
        assertEquals(0, result.getSyncAttempt().getSyncableLineItems().size());
        assertNotNull(result.getSyncAttempt().getAttemptSyncDate());
        assertEquals(result.getSyncAttempt().getSyncAttempts(), requisition.getSfmsSyncAttempts());
    }

    @Test // Can't test since I don't know how to exactly
    public void testErrorSync() {
        // Initialize test state.
        Requisition requisition = buildRequisition(1005, setOf(lineItem(1, true)));

        LocalDateTime expectedSyncDateTime = LocalDateTime.now();
        dummyDateTime.setDateTime(expectedSyncDateTime);

        // Execute method to test
        RequisitionSyncResult result = service.syncRequisition(requisition);

        // Check for valid side effects.
        assertEquals(SyncStatus.ERROR, result.getUpdatedRequisition().getSfmsSyncStatus());
        assertEquals(1, result.getUpdatedRequisition().getSfmsSyncAttempts());
        assertNull(result.getUpdatedRequisition().getSfmsSkippedReason());
        assertEquals(expectedSyncDateTime, result.getUpdatedRequisition().getLastSfmsSyncDateTime().get());

        assertEquals(SyncStatus.ERROR, result.getSyncAttempt().getOutcomeSyncStatus());
        assertFalse(result.getSyncAttempt().getWasSuccessful());
        assertNotNull(result.getSyncAttempt().getErrorInfo());
        assertEquals(1, result.getSyncAttempt().getSyncableLineItems().size());
        assertEquals(1, result.getSyncAttempt().getSyncAttempts());
        assertEquals(expectedSyncDateTime, result.getSyncAttempt().getAttemptSyncDate());
    }

    @Test // Can't test this due to pessimistic lock issue with modified date time
    public void testMultipleErrorSync() {
        // Initialize test state.
        Requisition requisition = buildRequisition(1006, setOf(lineItem(1, true)));

        LocalDateTime expectedSyncDateTime = LocalDateTime.now();
        dummyDateTime.setDateTime(expectedSyncDateTime);

        // Execute method to test
        RequisitionSyncResult result = service.syncRequisition(requisition);
        RequisitionSyncResult result1 = service.syncRequisition(result.getUpdatedRequisition());
        RequisitionSyncResult result2 = service.syncRequisition(requisition);
        RequisitionSyncResult result3 = service.syncRequisition(requisition);
        System.out.println(result3.getUpdatedRequisition().getSfmsSyncAttempts());
    }

    @Test // Can't test this due to pessimistic lock issue with modified date time
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
