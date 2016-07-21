package gov.nysenate.ess.supply.sfms;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.supply.requisition.service.RequisitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supply/sfms")
public class SupplySfmsRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(SupplySfmsRestApiCtrl.class);

    @Autowired private RequisitionService requisitionService;

    /**
     * Called on completion of an SFMS synchronization process.
     *
     * @param csvInsertedRequisitions a CSV listing of requisition id's that
     *                                were successfully inserted.
     */
    // TODO: permissions, what user will oracle log in as?
    @RequestMapping(value = "/synch", method = RequestMethod.POST, consumes = "text/plain")
    public BaseResponse sfmsSynchronizationResults(@RequestBody(required = false) String csvInsertedRequisitions) {
        if (csvInsertedRequisitions == null) {
            logger.info("SFMS Synchronization has finished, saved 0 requisitions.");
        }
        else {
            List<Integer> requisitionIds = Arrays.asList(csvInsertedRequisitions.split(","))
                                                 .stream()
                                                 .map(Integer::valueOf)
                                                 .collect(Collectors.toList());
            requisitionService.savedInSfms(requisitionIds);
            logger.info("SFMS Synchronization has finished, saved " + requisitionIds.size() + " requisitions.");
        }
        return new SimpleResponse();
    }
}
