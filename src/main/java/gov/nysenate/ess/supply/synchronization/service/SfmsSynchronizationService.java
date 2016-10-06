package gov.nysenate.ess.supply.synchronization.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.service.notification.slack.service.SlackChatService;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;
import gov.nysenate.ess.supply.requisition.service.RequisitionService;
import gov.nysenate.ess.supply.requisition.view.RequisitionView;
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
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

@Service
public class SfmsSynchronizationService {

    private static final Logger logger = LoggerFactory.getLogger(SfmsSynchronizationService.class);

    @Value("${scheduler.supply.sfms_synchronization.enabled}")
    private boolean synchronizationEnabled;
    @Autowired private RequisitionService requisitionService;
    @Autowired private SfmsSynchronizationProcedure synchronizationProcedure;
    @Autowired private ObjectMapper xmlObjectMapper;
    @Autowired private DateTimeFactory dateTimeFactory;
    @Autowired
    private SlackChatService slackChatService;

    /**
     * Inserts supply requisitions into SFMS for all requisitions where savedInSfms = <code>false</code>
     * On success, savedInSfms gets set to <code>true</code>.
     * <p>
     * Checks all requisitions, so any errors in previous runs will be
     * automatically attempted again in the next run.
     * <p>
     * Should be run at the top of each hour.
     */
    @Scheduled(cron = "${scheduler.supply.sfms_synchronization.cron}")
    public void synchronizeRequisitions() {
        // Only run if enabled in app.properties.
        if (!synchronizationEnabled) {
            return;
        }
        // Get all requisitions not saved in sfms since app release in 2016.
        LocalDateTime start = LocalDateTime.of(2016, 1, 1, 0, 0);
        LocalDateTime end = dateTimeFactory.now();
        StringBuffer sb = new StringBuffer();
        Range<LocalDateTime> dateRange = Range.closed(start, end);
        List<Requisition> requisitions = requisitionService.searchRequisitions("All", "All",
                                                                               EnumSet.of(RequisitionStatus.APPROVED),
                                                                               dateRange, "approved_date_time",
                                                                               "false", LimitOffset.ALL, "All").getResults();
        logger.info("Synchronizing {} requisitions with SFMS.", requisitions.size());
        for (Requisition requisition : requisitions) {
            try {
                synchronizationProcedure.synchronizeRequisition(toXml(requisition));
                requisitionService.savedInSfms(requisition.getRequisitionId(), true); // try to sync
            } catch (DataAccessException ex) {
                String msg = "Error synchronizing requisition " + requisition.getRequisitionId()
                        + " with SFMS. Exception is : " + ex.getMessage();
                logger.error(msg);
                sb.append(msg + "\n");
                requisitionService.savedInSfms(requisition.getRequisitionId(), false); // fail to sync
            }
        }
        if (sb.length() > 0)
            sendMessageToSlack(sb.toString());
    }

    /**
     * Send error message to slack channel
     *
     * @param s msg
     */
    private void sendMessageToSlack(String s) {
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date dateobj = new Date();
        slackChatService.sendMessage(df.format(dateobj) + " Sfms Synchronization Errors: \n");
        slackChatService.sendMessage(s);
    }

    /**
     * Serialize requisition to xml. Does not use {@link OutputUtils} because the SFMS
     * procedure expects dates to be ISO strings.
     */
    private String toXml(Requisition requisition) {
        String xml = null;
        try {
            xml = xmlObjectMapper.writeValueAsString(new RequisitionView(requisition));
        } catch (JsonProcessingException e) {
            logger.error("Error serializing requisition with id = " + requisition.getRequisitionId() + ".", e);
        }
        return xml;
    }
}
