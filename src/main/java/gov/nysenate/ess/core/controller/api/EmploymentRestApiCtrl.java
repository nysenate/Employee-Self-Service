package gov.nysenate.ess.core.controller.api;

import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.service.mail.InternshipSeason;
import gov.nysenate.ess.core.service.mail.PotentialEmployeeRejectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/employment")
public class EmploymentRestApiCtrl extends BaseRestApiCtrl{

    private static final Logger logger = LoggerFactory.getLogger(EmploymentRestApiCtrl.class);

    private final PotentialEmployeeRejectionService potentialEmployeeRejectionService;

    @Autowired
    public EmploymentRestApiCtrl(PotentialEmployeeRejectionService potentialEmployeeRejectionService) {
        this.potentialEmployeeRejectionService = potentialEmployeeRejectionService;
    }

    /**
     * Send Intern Rejection Emails API
     * -----------------------------
     * Get a list of years that the employee was active
     *
     * Usage:       (GET) /api/v1/employment/internship/rejection
     *
     * Request Params:
     * @param year Integer - required - the year of the internhsip
     * @param season String - required - the season of the internship SPRING, SUMMER, FALL
     * @return {@link ListViewResponse} of integers containing active years
     */
    @RequestMapping(value = "/internship/rejection")
    public SimpleResponse getEmployeeYearsActive(@RequestParam(required = true) Integer year,
                                                 @RequestParam(required = true) String season) {

        InternshipSeason internshipSeason = InternshipSeason.valueOf(season);

        if (internshipSeason == null) {
            return new SimpleResponse(false, "Wrong internship season submitted", "rejection-emails");
        }
        else {
            try {
                potentialEmployeeRejectionService.ProcessInternEmails(year, internshipSeason);
            } catch (FileNotFoundException e) {
                return new SimpleResponse(false, "File not found exception", "rejection-emails");
            }

            return new SimpleResponse(true, "Applicants Successfully Rejected", "rejection-emails");
        }

    }

}
