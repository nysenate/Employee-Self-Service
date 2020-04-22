package gov.nysenate.ess.core.controller.api;

import com.fasterxml.jackson.databind.JsonNode;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.service.pec.external.MoodleRecordService;
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
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/personnel/task/moodle/")
public class MoodleApiCtrl extends BaseRestApiCtrl {

    private MoodleRecordService moodleRecordService;
    private PersonnelTaskAssignmentDao personnelTaskAssignmentDao;
    final LocalDateTime jan1970 = LocalDateTime.of(1970,1,1,0,0);
    final LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);

    private static final Logger logger = LoggerFactory.getLogger(MoodleApiCtrl.class);

    @Autowired
    public MoodleApiCtrl(MoodleRecordService moodleRecordService,
                         PersonnelTaskAssignmentDao personnelTaskAssignmentDao) {
        this.moodleRecordService = moodleRecordService;
        this.personnelTaskAssignmentDao = personnelTaskAssignmentDao;
    }

    /**
     * Personnel Employee Task - Moodle Import
     * ---------------------------------------
     *
     * ESS contacts moodle for course data
     *
     * Usage:
     * (GET)    /api/v1/personnel/task/moodle/generate
     *
     * @Param from, the beginning of the date range needed for the records
     * @Param to, the end of the date range needed for the records
     *
     * @Param Organization, is always senate
     *
     *
     * @return String
     * */
    @RequestMapping(value = "/generate", method = {GET})
    @ResponseStatus(value = HttpStatus.OK)
    public SimpleResponse runMoodleImport(HttpServletRequest request,
                                        HttpServletResponse response ,
                                        @RequestParam(required = false, defaultValue = "1970") String from,
                                        @RequestParam(required = false, defaultValue = "tomorrow") String to,
                                        @RequestParam(required = false, defaultValue = "senate") String organization)  {
        checkPermission(ADMIN.getPermission());

        LocalDateTime ldtFrom;
        LocalDateTime ldtTo;

        //Set From
        if (from.equals("1970")) {
            ldtFrom = jan1970;
        }
        else {
            ldtFrom = stringToLocalDateTime(from);
        }

        //Set To
        if (to.equals("tomorrow")) {
            ldtTo = tomorrow;
        }
        else {
            ldtTo = stringToLocalDateTime(to);
        }

        //Contact moodle
        try {
            JsonNode json = moodleRecordService.contactMoodleForRecords(ldtFrom, ldtTo, organization);
            moodleRecordService.processMoodleEmployeeRecords(moodleRecordService.getMoodleRecordsFromJson(json.toString()));
        }
        catch (Exception e) {
            logger.info("Error contacting moodle for records", e);
            return new SimpleResponse(false, "Moode Report Generation", e.getMessage());
        }


        return new SimpleResponse(true, "Moode Report Generation", "moodle-report-generation");
    }

    private LocalDateTime stringToLocalDateTime(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(time, formatter);
    }
}
