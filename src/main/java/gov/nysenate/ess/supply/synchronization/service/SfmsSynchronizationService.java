package gov.nysenate.ess.supply.synchronization.service;

import gov.nysenate.ess.core.service.notification.slack.service.SlackChatService;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.requisition.model.*;
import gov.nysenate.ess.supply.requisition.service.RequisitionService;
import gov.nysenate.ess.supply.requisition.view.SfmsRequisitionView;
import gov.nysenate.ess.supply.synchronization.dao.SfmsSynchronizationProcedure;
import gov.nysenate.ess.supply.synchronization.dao.SyncAttemptDao;
import gov.nysenate.ess.supply.synchronization.model.RequisitionSyncAttempt;
import gov.nysenate.ess.supply.synchronization.model.RequisitionSyncResult;
import gov.nysenate.ess.supply.util.date.DateTimeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This service controls the execution of the SfmsSynchronizationProcedure.
 */
@Service
public class SfmsSynchronizationService {

    private static final Logger logger = LoggerFactory.getLogger(SfmsSynchronizationService.class);

    private final boolean synchronizationEnabled;
    private final RequisitionService requisitionService;
    private final SfmsSynchronizationProcedure synchronizationProcedure;
    private final DateTimeFactory dateTimeFactory;
    private final SlackChatService slackChatService;
    private final SyncAttemptDao syncAttemptDao;

    @Autowired
    public SfmsSynchronizationService(@Value("${scheduler.supply.sfms_synchronization.enabled}")
                                      boolean synchronizationEnabled,
                                      RequisitionService requisitionService,
                                      SfmsSynchronizationProcedure synchronizationProcedure,
                                      DateTimeFactory dateTimeFactory,
                                      SlackChatService slackChatService,
                                      SyncAttemptDao syncAttemptDao) {
        this.synchronizationEnabled = synchronizationEnabled;
        this.requisitionService = requisitionService;
        this.synchronizationProcedure = synchronizationProcedure;
        this.dateTimeFactory = dateTimeFactory;
        this.slackChatService = slackChatService;
        this.syncAttemptDao = syncAttemptDao;
    }

    /**
     * Updates inventory counts in SFMS for each completed Requisition.
     * <p>
     * Line items of 0 quantity and items not tracked in SFMS are stripped out because
     * they are not tracked in the SFMS inventory.
     * <p>
     * Checks all requisitions, so any errors in previous runs will be
     * automatically attempted again in the next run.
     * </p>
     * <p>
     * app.properties configuration:
     * - 'scheduler.supply.sfms_synchronization.enabled': boolean, determines if the synchronization process should run.
     * - 'scheduler.supply.sfms_synchronization.cron': Spring cron string specifying when the synchronization should run.
     * </p>
     */
    @Scheduled(cron = "${scheduler.supply.sfms_synchronization.cron}")
    public void synchronizeRequisitions() {
        // Only run if enabled in app.properties.
        if (!synchronizationEnabled) {
            return;
        }
        List<Requisition> reqsPendingSync = requisitionsToBeSynced();
        for (Requisition req : reqsPendingSync) {
            RequisitionSyncResult result = syncRequisition(req);
            requisitionService.saveRequisitionMetadata(result.updatedRequisition());
            syncAttemptDao.insertRequisitionSyncAttempt(result.syncAttempt());
        }
    }

    /**
     * @param requisition An approved or rejected requisition that has not yet been successfully synced
     * @return
     */
    public RequisitionSyncResult syncRequisition(Requisition requisition) {
        RequisitionSyncAttempt attempt = new RequisitionSyncAttempt(
                requisition.getRequisitionId(),
                requisition.getRevisionId(),
                requisition.getSyncAttemptCount() + 1,
                dateTimeFactory.now()
        );

        if (isRejected(requisition)) {
            return rejected(requisition, attempt);
        }

        if (hasNoSyncableItems(requisition)) {
            return noSyncableItems(requisition, attempt);
        }

        logger.info("Attempting to synchronize requisition {} with SFMS.", requisition.getRequisitionId());

        Requisition filteredReq = filterRequisition(requisition);
        try {
            synchronizationProcedure.synchronizeRequisition(OutputUtils.toXml(new SfmsRequisitionView(filteredReq)));
        } catch (DataAccessException ex) {
            String msg = "Error synchronizing requisition " + requisition.getRequisitionId()
                    + " with SFMS. Exception is : " + ex.getMessage();
            logger.error(msg);
            sendMessageToSlack(msg);
            return error(requisition, attempt, msg);
        }

        return success(requisition, attempt);
    }

    private RequisitionSyncResult rejected(Requisition req, RequisitionSyncAttempt attempt) {
        req = applyAttemptMetadata(req, attempt);
        req = req.setSyncStatus(SyncStatus.SKIPPED);
        req = req.setSfmsSkippedReason(SkippedReason.REJECTED);
        attempt.setOutcomeSyncStatus(SyncStatus.SKIPPED);
        attempt.setWasSuccessful(true);
        return new RequisitionSyncResult(attempt, req);
    }

    private RequisitionSyncResult noSyncableItems(Requisition req, RequisitionSyncAttempt attempt) {
        req = applyAttemptMetadata(req, attempt);
        req = req.setSyncStatus(SyncStatus.SKIPPED);
        req = req.setSfmsSkippedReason(SkippedReason.NO_SYNCABLE_ITEMS);
        attempt.setOutcomeSyncStatus(SyncStatus.SKIPPED);
        attempt.setWasSuccessful(true);
        return new RequisitionSyncResult(attempt, req);
    }

    private RequisitionSyncResult success(Requisition req, RequisitionSyncAttempt attempt) {
        req = applyAttemptMetadata(req, attempt);
        req = req.setSyncStatus(SyncStatus.COMPLETE);
        attempt.setOutcomeSyncStatus(SyncStatus.COMPLETE);
        attempt.setWasSuccessful(true);
        attempt.setSyncedItemIds(lineItemIds(lineItemsRequiringSync(req.getLineItems())));
        return new RequisitionSyncResult(attempt, req);
    }

    private RequisitionSyncResult error(Requisition req, RequisitionSyncAttempt attempt, String error) {
        req = applyAttemptMetadata(req, attempt);
        req = req.setSyncStatus(SyncStatus.ERROR);
        attempt.setOutcomeSyncStatus(SyncStatus.ERROR);
        attempt.setWasSuccessful(false);
        attempt.setErrorMsg(error);
        attempt.setSyncedItemIds(lineItemIds(lineItemsRequiringSync(req.getLineItems())));
        return new RequisitionSyncResult(attempt, req);
    }

    private Requisition applyAttemptMetadata(Requisition req, RequisitionSyncAttempt attempt) {
        req = req.setSyncAttemptCount(attempt.getAttemptCount());
        req = req.setLastSfmsSyncDateTimeDateTime(attempt.getAttemptDateTime());
        return req;
    }

    private List<Integer> lineItemIds(Set<LineItem> lineItems) {
        return lineItems.stream()
                .map(li -> li.getItem().getId())
                .collect(Collectors.toList());
    }

    private boolean isRejected(Requisition requisition) {
        return RequisitionStatus.REJECTED.equals(requisition.getStatus());
    }

    private boolean hasNoSyncableItems(Requisition requisition) {
        return lineItemsRequiringSync(requisition.getLineItems()).isEmpty();
    }

    /**
     * Gets all requisitions which have not yet been synced with SFMS.
     *
     * @return
     */
    private List<Requisition> requisitionsToBeSynced() {
        RequisitionQuery query = new RequisitionQuery()
                .setStatuses(EnumSet.of(RequisitionStatus.APPROVED, RequisitionStatus.REJECTED))
                .setFromDateTime(LocalDateTime.of(2016, 1, 1, 0, 0)) // Date before supply was launched, so includes all requisitions.
                .setToDateTime(dateTimeFactory.now())
                .setSyncStatus(EnumSet.of(SyncStatus.PENDING, SyncStatus.ERROR))
                .setDateField("ordered_date_time")
                .setLimitOffset(LimitOffset.ALL);

        return requisitionService.searchRequisitions(query).getResults();
    }

    /**
     * Removes line items of 0 quantity and items that are not tracked in inventory from a requisition.
     * These items should not be synchronized with SFMS.
     */
    private Requisition filterRequisition(Requisition requisitions) {
        return requisitions.setLineItems(lineItemsRequiringSync(requisitions.getLineItems()));
    }

    private Set<LineItem> lineItemsRequiringSync(Set<LineItem> lineItems) {
        return lineItems.stream()
                .filter(lineItem -> lineItem.getQuantity() > 0 && lineItem.getItem().requiresSynchronization())
                .collect(Collectors.toSet());
    }

    /**
     * Send error message to slack channel
     *
     * @param s msg
     */
    private void sendMessageToSlack(String s) {
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date dateobj = new Date();
        slackChatService.sendMessage(df.format(dateobj) + " Sfms Synchronization Errors: " + s + "\n");
    }
}
