package gov.nysenate.ess.core.controller.api.pec;

import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.service.pec.external.knowbe4.KnowBe4RecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

import static gov.nysenate.ess.core.model.auth.SimpleEssPermission.ADMIN;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/knowbe4")
public class KnowBe4ApiCtrl extends BaseRestApiCtrl {

    private KnowBe4RecordService knowBe4RecordService;
    private PersonnelTaskAssignmentDao personnelTaskAssignmentDao;

    private static final Logger logger = LoggerFactory.getLogger(KnowBe4ApiCtrl.class);

    @Autowired
    public KnowBe4ApiCtrl(KnowBe4RecordService knowBe4RecordService, PersonnelTaskAssignmentDao taskAssignmentDao) {
        this.knowBe4RecordService = knowBe4RecordService;
        this.personnelTaskAssignmentDao = taskAssignmentDao;
    }

    /**
     * KnowBe4 - Cache Refresh
     * ---------------------------------------
     *
     * ESS refreshes its KnowBe4 assignment id cache
     *
     * This is necessary for handling a new task without restarting ESS
     *
     * Usage:
     * (GET)    /api/v1/knowbe4/cache/refresh
     *
     *
     * @return String
     * */
    @RequestMapping(value = "/cache/refresh", method = {GET})
    @ResponseStatus(value = HttpStatus.OK)
    public SimpleResponse refreshEverfiCaches(HttpServletRequest request,
                                              HttpServletResponse response) {
        checkPermission(ADMIN.getPermission());
        knowBe4RecordService.refreshCaches();
        return new SimpleResponse(true, "KnowBe4 Caches Refreshed", "knowbe4-cache-refresh");
    }

    /**
     * KnowBe4 - Personnel Employee Task Data Import
     * --------------------------------------------
     *
     * ESS contacts KnowBe4 for course data
     *
     * Usage:
     * (POST)    /api/v1/knowbe4/personnel/task/generate
     *
     * @Param from, the beginning of the date range needed for the records
     *
     * @Param Organization, is always senate
     *
     *
     * @return String
     * */
    @RequestMapping(value = "/personnel/task/generate", method = GET)
    @ResponseStatus(value = HttpStatus.OK)
    public SimpleResponse runKnowBe4Import(HttpServletRequest request,
                                          HttpServletResponse response) {
        checkPermission(ADMIN.getPermission());

        //Contact KnowBe4
        try {
            knowBe4RecordService.contactKnowBe4ForRecords();
        }
        catch (Exception e) {
            logger.info("Error contacting KnowBe4 for records", e);
            return new SimpleResponse(false, e.getMessage(), "knowbe4-report-generation");
        }

        return new SimpleResponse(true, "KnowBe4 Report Generation", "KnowBe4-report-generation");
    }
}
