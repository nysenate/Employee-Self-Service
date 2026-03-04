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
        List<Requisition> filteredReqs = filterRequisitions(originalReqs);

        for (int i = 0; i < filteredReqs.size(); i++) {
            Requisition r = filteredReqs.get(i);
            boolean success = syncRequisition(r);
            ModifySyncStatus modify = new ModifySyncStatus();
            r = modify.modifySyncStatuses(r, success);

            Requisition modified = updateOriginalReq(originalReqs.get(i), r, success);

            requisitionService.saveRequisition(modified);
        }

    }

    private boolean syncRequisition(Requisition requisition) {
        if (requiresSync(requisition)) {
            logger.info("Attempting to synchronize requisition {} with SFMS.", requisition.getRequisitionId());
            try {
                //requisition = requisition.setLastSfmsSyncDateTimeDateTime(dateTimeFactory.now());
                synchronizationProcedure.synchronizeRequisition(OutputUtils.toXml(new SfmsRequisitionView(requisition)));

            } catch (DataAccessException ex) {
                String msg = "Error synchronizing requisition " + requisition.getRequisitionId()
                        + " with SFMS. Exception is : " + ex.getMessage();
                logger.error(msg);
                sendMessageToSlack(msg);
                return false;
            }
        }else{
            return false;
        }

//            setAsSynced(requisition);

        return true;
    }


    /**
     * <p>Copies all the values from the filtered reqs to the original after all the modifications are done</p>
     *
     * @param original - the original req from the db
     * @param filteredReq - the req that had its line items removed and sync status and everything related updated
     * @param wasSuccessful - Whether the requisition was saved to sfms or not
     * @return
     */
    public Requisition updateOriginalReq(Requisition original, Requisition filteredReq, boolean wasSuccessful) {
        original = original.setSyncStatus(filteredReq.getSfmsSyncStatus());
        original = original.setSfmsSkippedReason(filteredReq.getSfmsSkippedReason());
        original = original.setSavedInSfms(wasSuccessful);

        original = original.setSfmsSyncAttempts(filteredReq.getSfmsSyncAttempts());

        return original;
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
                .setDateField("ordered_date_time")
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

    public Set<LineItem> getLineItemsRequiringSync(Set<LineItem> lineItems){
        return lineItemsRequiringSync(lineItems);
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

    public Requisition changeRequisitionBySyncStatus(Requisition r){
        if (r.getStatus().equals(RequisitionStatus.REJECTED)) {
            r = r.setSfmsSkippedReason(SkippedReason.REJECTED);
            r = r.setSyncStatus(SyncStatus.SKIPPED);
            r = r.setSfmsSyncAttempts(r.getSfmsSyncAttempts() + 1);
        } else if (r.getLineItems().isEmpty() && r.getStatus().equals(RequisitionStatus.REJECTED)) { // edge case you explained
            r = r.setSfmsSkippedReason(SkippedReason.REJECTED);
            r = r.setSyncStatus(SyncStatus.SKIPPED);
            r = r.setSfmsSyncAttempts(r.getSfmsSyncAttempts() + 1);
        } else if (lineItemsRequiringSync(r.getLineItems()).isEmpty()) {
            r = r.setSfmsSkippedReason(SkippedReason.NO_SYNCABLE_ITEMS);
            r = r.setSyncStatus(SyncStatus.SKIPPED);
        } else {
            r = r.setSyncStatus(SyncStatus.PENDING);
        }

        return r;
    }
}
