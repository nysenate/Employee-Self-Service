package gov.nysenate.ess.supply.synchronization.service;

import gov.nysenate.ess.core.service.notification.slack.service.SlackChatService;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.requisition.model.*;
import gov.nysenate.ess.supply.requisition.service.RequisitionService;
import gov.nysenate.ess.supply.requisition.view.SfmsRequisitionView;
import gov.nysenate.ess.supply.synchronization.SyncStatuses.ModifySyncStatus;
import gov.nysenate.ess.supply.synchronization.dao.SfmsSynchronizationProcedure;
import gov.nysenate.ess.supply.synchronization.dao.SyncAttemptDao;
import gov.nysenate.ess.supply.synchronization.model.RequisitionSyncAttempt;
import gov.nysenate.ess.supply.synchronization.model.RequisitionSyncResult;
import gov.nysenate.ess.supply.util.date.DateTimeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
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
     * Inserts supply requisition line items into SFMS for all approved requisitions where savedInSfms = <code>false</code>.
     * On success, savedInSfms gets set to <code>true</code>.
     * Line items of 0 quantity and items not tracked in SFMS are filtered out so they do not get synced.
     * If after filtering, a requisiton has no other line items, it will be marked as synced in supply but will not be synced with SFMS.
     * <p>
     * Checks all requisitions, so any errors in previous runs will be
     * automatically attempted again in the next run.
     * </p>
     * <p>
     * app.properties configuration:
     * - 'scheduler.supply.sfms_synchronization.enabled': boolean, determines if the synchronization process should run.
     * - 'scheduler.supply.sfms_synchronization.cron': Spring cron string specifying when the synchronization should run.
     * </p>
     *
     * <p>Also determines the state the Requistion should be on depending on its current state before trying to synchronize.</p>
     */
    @Scheduled(cron = "${scheduler.supply.sfms_synchronization.cron}")
    public void synchronizeRequisitions() {
        // Only run if enabled in app.properties.
        if (!synchronizationEnabled) {
            return;
        }
        List<Requisition> originalReqs = requisitionsToBeSynced();
        for (Requisition req : originalReqs) {
            RequisitionSyncResult result = syncRequisition(req);

            requisitionService.saveRequisition(result.getUpdatedRequisition());
            syncAttemptDao.insertRequisitionSyncAttempt(result.getSyncAttempt());
        }

    }

    public RequisitionSyncResult syncRequisition(Requisition requisition) {
        boolean wasSuccessful = false;
        String errorMessage = null;
        Requisition filteredReq = filterRequisition(requisition);

        if (requiresSync(filteredReq)) {
            logger.info("Attempting to synchronize requisition {} with SFMS.", requisition.getRequisitionId());
            try {
                synchronizationProcedure.synchronizeRequisition(OutputUtils.toXml(new SfmsRequisitionView(requisition)));
                wasSuccessful = true;
            } catch (DataAccessException ex) {
                String msg = "Error synchronizing requisition " + requisition.getRequisitionId()
                        + " with SFMS. Exception is : " + ex.getMessage();
                errorMessage = msg;
                logger.error(msg);
                sendMessageToSlack(msg);
            }
        }

        RequisitionSyncResult result = applySideEffects(wasSuccessful, errorMessage, requisition, filteredReq);

        requisition = requisitionService.saveRequisition(result.getUpdatedRequisition());
        result.setUpdatedRequisition(requisition);

        return result;
    }

    /**
     * Handles all of the side effects of saving a requisition to sfms db such as getting the sync status, updating the original requisition, and creating a sync attempt for that requisition to see what the outcome of the synchronization procedure was for the given requisition.
     *
     * @return
     */
    public RequisitionSyncResult applySideEffects(boolean wasSuccessful, String errorMessage, Requisition requisition, Requisition filteredReq) {
        ModifySyncStatus modify = new ModifySyncStatus();
        RequisitionSyncAttempt syncAttempt;
        filteredReq = modify.modifySyncStatuses(filteredReq, wasSuccessful);
        requisition = updateOriginalReq(requisition, filteredReq, wasSuccessful);
        syncAttempt = fillRequisitionSyncAttempt(filteredReq, errorMessage, wasSuccessful);
        return new RequisitionSyncResult(syncAttempt, requisition);
    }

    /**
     * <p>Updates the syncAttempt with information that will define a recollection of synchronization attempts for a requisition.</p>
     *
     * @param filteredRequisition - The requisition that has gone through the synchronization process and has key information that is useful in defining the history of a particular requisition.
     * @param errorMessage        - The error message which may or may not be null depending on if there was an error during synchronizing.
     * @param wasSuccessful       - the flag which indicates whether the requisition was successfully saved to the SFMS db.
     * @return
     */
    public RequisitionSyncAttempt fillRequisitionSyncAttempt(Requisition filteredRequisition, String errorMessage, boolean wasSuccessful) {
        RequisitionSyncAttempt syncAttempt = new RequisitionSyncAttempt();

        syncAttempt.setErrorInfo(errorMessage);
        syncAttempt.setWasSuccessful(wasSuccessful);
        syncAttempt.setRequisitionId(filteredRequisition.getRequisitionId());
        syncAttempt.setSyncAttempts(filteredRequisition.getSfmsSyncAttempts());
        syncAttempt.setOutcomeSyncStatus(filteredRequisition.getSfmsSyncStatus());
        syncAttempt.setAttemptSyncDate(dateTimeFactory.now());
        List<Integer> itemIds = new ArrayList<>();
        for (LineItem lineItem : filteredRequisition.getLineItems()) {
            itemIds.add(lineItem.getItem().getId());
        }
        syncAttempt.setSyncableLineItems(itemIds);
        return syncAttempt;
    }


    /**
     * <p>Copies all the values from the filtered reqs to the original after all the modifications are done</p>
     *
     * @param original      - the original req from the db
     * @param filteredReq   - the req that had its line items removed and sync status and everything related updated
     * @param wasSuccessful - Whether the requisition was saved to sfms or not
     * @return
     */
    public Requisition updateOriginalReq(Requisition original, Requisition filteredReq, boolean wasSuccessful) {
        original = original.setSyncStatus(filteredReq.getSfmsSyncStatus());
        original = original.setSfmsSkippedReason(filteredReq.getSfmsSkippedReason());
        original = original.setSavedInSfms(wasSuccessful);

        original = original.setSfmsSyncAttempts(filteredReq.getSfmsSyncAttempts());
        original = original.setLastSfmsSyncDateTimeDateTime(dateTimeFactory.now());
        return original;
    }

    private boolean requiresSync(Requisition requisition) {
        return requisition.getLineItems().size() > 0 && requisition.getStatus().equals(RequisitionStatus.APPROVED);
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
        Requisition filtered = requisitions.setLineItems(lineItemsRequiringSync(requisitions.getLineItems()));

        return filtered;
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
