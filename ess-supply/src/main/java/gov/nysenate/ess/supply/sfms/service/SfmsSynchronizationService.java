package gov.nysenate.ess.supply.sfms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;
import gov.nysenate.ess.supply.requisition.service.RequisitionService;
import gov.nysenate.ess.supply.requisition.view.RequisitionView;
import gov.nysenate.ess.supply.sfms.dao.SfmsSynchronizationProcedure;
import gov.nysenate.ess.supply.util.date.DateTimeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

@Service
public class SfmsSynchronizationService {

    private static final Logger logger = LoggerFactory.getLogger(SfmsSynchronizationService.class);

    @Value("${supply.synchronization.cron.enabled}") private boolean synchronizationEnabled;
    @Autowired private RequisitionService requisitionService;
    @Autowired private SfmsSynchronizationProcedure synchronizationProcedure;
    @Autowired private ObjectMapper xmlObjectMapper;
    @Autowired private DateTimeFactory dateTimeFactory;

    /**
     * Inserts supply requisitions into SFMS for all requisitions where savedInSfms = <code>false</code>
     * On success, savedInSfms gets set to <code>true</code>.
     *
     * Checks all requisitions, so any errors in previous runs will be
     * automatically attempted again in the next run.
     *
     * Should be run at the top of each hour.
     */
    @Scheduled(cron = "${supply.sfms.synchronization.cron}")
    public void synchronizeRequisitions() {
        // Only run if enabled in app.properties.
        if (!synchronizationEnabled) {
            return;
        }
        // Get all requisitions not saved in sfms since app release in 2016.
        LocalDateTime start = LocalDateTime.of(2016, 1, 1, 0, 0);
        LocalDateTime end = dateTimeFactory.now();
        Range<LocalDateTime> dateRange = Range.closed(start, end);
        List<Requisition> requisitions = requisitionService.searchRequisitions("any", "any",
                                                                               EnumSet.of(RequisitionStatus.APPROVED),
                                                                               dateRange, "approved_date_time",
                                                                               "false", LimitOffset.ALL).getResults();
        logger.info("Synchronizing {} requisitions with SFMS.", requisitions.size());
        for (Requisition requisition : requisitions) {
            int result = synchronizationProcedure.synchronizeRequisition(toXml(requisition));
            if (result == requisition.getRequisitionId()) {
                requisitionService.savedInSfms(requisition.getRequisitionId());
            }
        }
    }

    /**
     * Serialize requisition to xml. Does not use {@link OutputUtils} because the SFMS
     * procedure expects dates to be ISO strings.
     * @param requisition
     * @return
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
