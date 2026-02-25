package gov.nysenate.ess.supply.synchronization.service;

import gov.nysenate.ess.core.service.notification.slack.service.SlackChatService;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.requisition.model.*;
import gov.nysenate.ess.supply.requisition.service.RequisitionService;
import gov.nysenate.ess.supply.requisition.view.SfmsRequisitionView;
import gov.nysenate.ess.supply.synchronization.dao.SfmsSynchronizationProcedure;
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

    @Autowired
    public SfmsSynchronizationService(@Value("${scheduler.supply.sfms_synchronization.enabled}")
                                      boolean synchronizationEnabled,
                                      RequisitionService requisitionService,
                                      SfmsSynchronizationProcedure synchronizationProcedure,
                                      DateTimeFactory dateTimeFactory,
                                      SlackChatService slackChatService) {
        this.synchronizationEnabled = synchronizationEnabled;
        this.requisitionService = requisitionService;
        this.synchronizationProcedure = synchronizationProcedure;
        this.dateTimeFactory = dateTimeFactory;
        this.slackChatService = slackChatService;
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
     */
    @Scheduled(cron = "${scheduler.supply.sfms_synchronization.cron}")
    public void synchronizeRequisitions() {
        // Only run if enabled in app.properties.
        if (!synchronizationEnabled) {
            return;
        }
        List<Requisition> reqs = requisitionsToBeSynced();
        List<Requisition> filteredReqs = filterRequisitions(reqs);
        for (Requisition r : filteredReqs) {
            syncRequisition(r);
        }


        for (Requisition r : reqs) {
            if (!filteredReqs.contains(r)) {
                if (r.getStatus().equals(RequisitionStatus.REJECTED)) {
                    r = r.setSfmsSkippedReason(SkippedReason.REJECTED);
                    r = r.setSyncStatus(SyncStatus.SKIPPED);
                } else if (r.getLineItems().isEmpty() && r.getStatus().equals(RequisitionStatus.REJECTED)) {
                    r = r.setSfmsSkippedReason(SkippedReason.REJECTED);
                    r = r.setSyncStatus(SyncStatus.SKIPPED);
                } else if (r.getLineItems().isEmpty()) {
                    r = r.setSfmsSkippedReason(SkippedReason.NO_SYNCABLE_ITEMS);
                    r = r.setSyncStatus(SyncStatus.SKIPPED);
                } else {
                    r = r.setSyncStatus(SyncStatus.PENDING);
                }
                System.out.println(r);
                requisitionService.saveRequisition(r);
            }
        }
    }

    private void syncRequisition(Requisition requisition) {
        if (requiresSync(requisition)) {
            logger.info("Attempting to synchronize requisition {} with SFMS.", requisition.getRequisitionId());
            try {
                requisition = requisition.setLastSfmsSyncDateTimeDateTime(dateTimeFactory.now());
                System.out.println("Did we get to the synchronization procedure.");
                synchronizationProcedure.synchronizeRequisition(OutputUtils.toXml(new SfmsRequisitionView(requisition)));
                System.out.println("Did we get through the entire saving synchronization procedure.");
                requisition = requisition.setSavedInSfms(true);
                requisition = requisition.setSyncStatus(SyncStatus.COMPLETE);
            } catch (DataAccessException ex) {
                requisition = requisition.setSyncStatus(SyncStatus.ERROR);
                String msg = "Error synchronizing requisition " + requisition.getRequisitionId()
                        + " with SFMS. Exception is : " + ex.getMessage();
                logger.error(msg);
                sendMessageToSlack(msg);
            } finally {
                System.out.println(requisition);
                requisition = requisition.setSfmsSyncAttempts(requisition.getSfmsSyncAttempts() + 1);
                requisitionService.saveRequisition(requisition);
            }
        } else {
            logger.info("Requisition {} can skip SFMS sync.", requisition.getRequisitionId());
            setAsSynced(requisition);
        }
    }

    private boolean requiresSync(Requisition requisition) {
        return requisition.getLineItems().size() > 0;
    }

    private void setAsSynced(Requisition requisition) {
        requisitionService.savedInSfms(requisition.getRequisitionId(), true);
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
                .setSavedInSfms(false)
                .setDateField("approved_date_time")
                .setLimitOffset(LimitOffset.ALL);

        return requisitionService.searchRequisitions(query).getResults();
    }

    /**
     * Removes line items of 0 quantity and items that are not tracked in inventory from a requisition.
     * These items should not be synchronized with SFMS.
     */
    private List<Requisition> filterRequisitions(List<Requisition> requisitions) {
        List<Requisition> filtered = new ArrayList<>();
        for (Requisition req : requisitions) {
            filtered.add(req.setLineItems(lineItemsRequiringSync(req.getLineItems())));
        }
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

    /**
     * sets the sync status depending on the current state of the requisition
     * <p>All cases of the Sync Status's</p>
     * <ul>
     *     <li>If there was a last sfms sync date and the req wasn't saved in sfms then there must've been an error when syncing</li>
     *
     *     <li>If the req has no line items to sync then it should be skipped for not having syncable items</li
     *     >
     *     <li>If the req has a rejected date time, then it was explicitly skipped</li>
     *
     *     <li>If the req has an approved date time and is saved to sfms then its completed</li>
     *
     *     <li>Otherwise, it should stay in pending</li>
     * </ul>
     *
     * @param requisition
     * @returns the requisition with the sync status properly set
     */
    public Requisition assignSyncStatus(Requisition requisition) {

        if (requisition.getLastSfmsSyncDateTime().isPresent() && !requisition.getSavedInSfms()) {
            requisition = requisition.setSyncStatus(SyncStatus.ERROR);
        } else if (requisition.getLineItems().isEmpty()) {
            requisition = requisition.setSfmsSkippedReason(SkippedReason.NO_SYNCABLE_ITEMS);
            requisition = requisition.setSyncStatus(SyncStatus.SKIPPED);
        } else if (requisition.getRejectedDateTime().isPresent()) {
            requisition = requisition.setSfmsSkippedReason(SkippedReason.REJECTED);
            requisition = requisition.setSyncStatus(SyncStatus.SKIPPED);
        } else if (requisition.getApprovedDateTime().isPresent() && requisition.getSavedInSfms()) {
            requisition = requisition.setSyncStatus(SyncStatus.COMPLETE);
            requisition = requisition.setSfmsSyncAttempts(requisition.getSfmsSyncAttempts() + 1);
        } else {
            requisition = requisition.setSyncStatus(SyncStatus.PENDING);
        }

        return requisition;
    }
}
