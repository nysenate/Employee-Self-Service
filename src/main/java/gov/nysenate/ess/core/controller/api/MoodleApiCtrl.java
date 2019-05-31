package gov.nysenate.ess.core.controller.api;

import com.fasterxml.jackson.databind.JsonNode;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.dao.pec.PersonnelAssignedTaskDao;
import gov.nysenate.ess.core.model.pec.MoodleEmployeeRecord;
import gov.nysenate.ess.core.service.pec.MoodleRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/personnel/task/moodle/")
public class MoodleApiCtrl extends BaseRestApiCtrl {

    private MoodleRecordService moodleRecordService;
    private PersonnelAssignedTaskDao personnelAssignedTaskDao;

    @Autowired
    public MoodleApiCtrl(MoodleRecordService moodleRecordService,
                         PersonnelAssignedTaskDao personnelAssignedTaskDao) {
        this.moodleRecordService = moodleRecordService;
        this.personnelAssignedTaskDao = personnelAssignedTaskDao;
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
    public void runMoodleImport(HttpServletRequest request,
                                          HttpServletResponse response,
                                     @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                     @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                                     @RequestParam String organization) throws IOException {

        //Change specifics of moodle api call once it is available
        JsonNode json = moodleRecordService.contactMoodleForRecords(from, to, organization);
        moodleRecordService.processMoodleEmployeeRecords(moodleRecordService.getMoodleRecordsFromJson(json.toString()));
    }
}
