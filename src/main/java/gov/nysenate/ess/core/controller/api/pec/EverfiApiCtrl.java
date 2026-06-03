package gov.nysenate.ess.core.controller.api.pec;

import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiRecordService;
import gov.nysenate.ess.core.service.pec.external.everfi.sync.EverfiUserSyncJobResult;
import gov.nysenate.ess.core.service.pec.external.everfi.sync.EverfiUserSyncJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static gov.nysenate.ess.core.model.auth.SimpleEssPermission.ADMIN;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/everfi")
public class EverfiApiCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(EverfiApiCtrl.class);

    final LocalDateTime jan1970 = LocalDateTime.of(1970, 1, 1, 0, 0, 1);
    final LocalDateTime lastYearJan = LocalDateTime.of(LocalDateTime.now().getYear() - 1, 1, 1, 0, 0, 1);
    final LocalDateTime now = LocalDateTime.now();
    final LocalDateTime threeMonthsAgo = now.minusDays(90);

    private EverfiRecordService everfiRecordService;
    private EverfiUserSyncJobService everfiUserSyncJobService;

    @Autowired
    public EverfiApiCtrl(EverfiRecordService everfiRecordService,
                         EverfiUserSyncJobService everfiUserSyncJobService
    ) {
        this.everfiRecordService = everfiRecordService;
        this.everfiUserSyncJobService = everfiUserSyncJobService;
    }


    /**
     * Everfi - Run User Sync
     * ---------------------------------------
     * <p>
     * Executes the load-plan-execute Everfi user sync pipeline and returns whether it completed
     * successfully. Defaults to a dry run and sends the detailed report email to PEC admins.
     * <p>
     * Usage:
     * (POST)    /api/v1/everfi/sync/users
     * (POST)    /api/v1/everfi/sync/users?dryRun=false
     * (POST)    /api/v1/everfi/sync/users?sendReportEmail=false
     *
     * @return {@link SimpleResponse}
     */
    @RequestMapping(value = "/sync/users", method = {POST})
    @ResponseStatus(value = HttpStatus.OK)
    public SimpleResponse runUserSync(
            @RequestParam(required = false, defaultValue = "true") boolean dryRun,
            @RequestParam(required = false, defaultValue = "true") boolean sendReportEmail) {
        checkPermission(ADMIN.getPermission());
        EverfiUserSyncJobResult result = everfiUserSyncJobService.runUserSync(dryRun, sendReportEmail);
        return new SimpleResponse(result.success(), result.message(), "everfi-sync-run");
    }

    /**
     * Everfi - Cache Refresh
     * ---------------------------------------
     * <p>
     * ESS refreshes its everfi content id cache and its assignment id cache
     * <p>
     * This is necessary for handling a new task without restarting ESS
     * <p>
     * Usage:
     * (GET)    /api/v1/everfi/cache/refresh
     *
     * @return String
     */
    @RequestMapping(value = "/cache/refresh", method = {GET})
    @ResponseStatus(value = HttpStatus.OK)
    public SimpleResponse refreshEverfiCaches(HttpServletRequest request,
                                              HttpServletResponse response) {
        checkPermission(ADMIN.getPermission());
        everfiRecordService.refreshCaches();
        return new SimpleResponse(true, "Everfi Caches Refreshed", "everfi-cache-refresh");
    }


    /**
     * Everfi - Personnel Employee Task Data Import
     * --------------------------------------------
     * <p>
     * ESS contacts Everfi for course data
     * <p>
     * Usage:
     * (POST)    /api/v1/everfi/personnel/task/generate
     * (POST)    /api/v1/everfi/personnel/task/generate?since=threeMonthsAgo
     * (POST)    /api/v1/everfi/personnel/task/generate?since=lastYear
     * (POST)    /api/v1/everfi/personnel/task/generate?since=1970
     *
     * @return String
     * @Param from, the beginning of the date range needed for the records
     * @Param Organization, is always senate
     */
    @RequestMapping(value = "/personnel/task/generate", method = POST)
    @ResponseStatus(value = HttpStatus.OK)
    public SimpleResponse runEverfiImport(HttpServletRequest request,
                                          HttpServletResponse response,
                                          @RequestParam(required = false, defaultValue = "1970") String since) {
        checkPermission(ADMIN.getPermission());

        LocalDateTime ldtsince;

        //Set From
        if (since.equals("1970")) {
            ldtsince = jan1970;
        } else if (since.equals("lastYear")) {
            ldtsince = lastYearJan;
        } else if (since.equals("threeMonthsAgo")) {
            ldtsince = threeMonthsAgo;
        } else {
            ldtsince = stringToLocalDateTime(since);
        }

        //Contact everfi
        try {
            logger.info(ldtsince.toString());
            everfiRecordService.contactEverfiForUserRecords(ldtsince.toString());
        } catch (Exception e) {
            logger.info("Error contacting Everfi for records", e);
            return new SimpleResponse(false, e.getMessage(), "everfi-report-generation");
        }

        return new SimpleResponse(true, "Everfi Report Generation", "everfi-report-generation");
    }

    private LocalDateTime stringToLocalDateTime(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(time, formatter);
    }

}
