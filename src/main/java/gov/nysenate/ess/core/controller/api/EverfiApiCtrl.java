package gov.nysenate.ess.core.controller.api;

import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiRecordService;
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
import java.time.format.DateTimeFormatter;

import static gov.nysenate.ess.core.model.auth.SimpleEssPermission.ADMIN;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/personnel/task/everfi/")
public class EverfiApiCtrl extends BaseRestApiCtrl {

    private EverfiRecordService everfiRecordService;
    private PersonnelTaskAssignmentDao personnelTaskAssignmentDao;
    final LocalDateTime jan1970 = LocalDateTime.of(1970,1,1,0,0,0,0);
    private static final Logger logger = LoggerFactory.getLogger(EverfiApiCtrl.class);

    @Autowired
    public EverfiApiCtrl(EverfiRecordService everfiRecordService, PersonnelTaskAssignmentDao personnelTaskAssignmentDao) {
        this.everfiRecordService = everfiRecordService;
        this.personnelTaskAssignmentDao = personnelTaskAssignmentDao;
    }


    /**
     * Personnel Employee Task - Everfi Import
     * ---------------------------------------
     *
     * ESS contacts Everfi for course data
     *
     * Usage:
     * (GET)    /api/v1/personnel/task/everfi/generate
     *
     * @Param from, the beginning of the date range needed for the records
     *
     * @Param Organization, is always senate
     *
     *
     * @return String
     * */
    @RequestMapping(value = "/generate", method = {GET})
    @ResponseStatus(value = HttpStatus.OK)
    public SimpleResponse runEverfiImport(HttpServletRequest request,
                                          HttpServletResponse response,
                                          @RequestParam(required = false, defaultValue = "1970") String since) {
//        checkPermission(ADMIN.getPermission());

        LocalDateTime ldtsince;

        //Set From
        if (since.equals("1970")) {
            ldtsince = jan1970;
        }
        else {
            ldtsince = stringToLocalDateTime(since);
        }

        //Contact everfi
        try {
            logger.info(ldtsince.toString() + ":00.000");
            everfiRecordService.contactEverfiForUserRecords(ldtsince.toString() + ":00.000");
        }
        catch (Exception e) {
            logger.info("Error contacting Everfi for records", e);
            return new SimpleResponse(false, "Everfi Report Generation", e.getMessage());
        }

        return new SimpleResponse(true, "Everfi Report Generation", "everfi-report-generation");
    }


    private LocalDateTime stringToLocalDateTime(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(time, formatter);
    }

}
