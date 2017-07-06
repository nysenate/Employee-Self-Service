package gov.nysenate.ess.time.controller.api;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.ess.core.client.view.base.DateRangeView;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.time.client.view.expectedhrs.ExpectedHoursView;
import gov.nysenate.ess.time.model.expectedhrs.ExpectedHours;
import gov.nysenate.ess.time.model.expectedhrs.InvalidExpectedHourDatesEx;
import gov.nysenate.ess.time.service.expectedhrs.ExpectedHoursService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/expectedhrs")
public class ExpectedHoursRestApiCtrl extends BaseRestApiCtrl {

    @Autowired ExpectedHoursService expectedHoursService;


    /**
     * Get Expected Hours API
     * -------------------
     *
     * Get expected work hours for the given time period,
     * in addition to the expected hours for the year to the begin date,
     * and the overall expected hours for the current year.
     *
     * (GET) /api/v1/expectedhrs
     *
     * Request Parameters:
     * @param empId - Integer - required - Expected Hours will be retrieved for this employee id
     * @param beginDate - String (ISO 8601 Date) - required - begin date of the time period
     *                  must be the same year as the end date
     * @param endDate - String (ISO 8601 Date) - required - end date of the time period
     *                must be the same year as the begin date
     *
     */
    @RequestMapping(value = "", method = {GET, HEAD})
    public ViewObjectResponse<ExpectedHoursView> getExpectedHours(@RequestParam int empId,
                                                                  @RequestParam String beginDate,
                                                                  @RequestParam String endDate) {
        LocalDate parsedBeginDate = parseISODate(beginDate, "beginDate");
        LocalDate parsedEndDate = parseISODate(endDate, "endDate");
        Range<LocalDate> dateRange = getClosedRange(parsedBeginDate, parsedEndDate,
                "beginDate", "endDate");

        ExpectedHours expectedHours = expectedHoursService.getExpectedHours(empId, dateRange);

        ExpectedHoursView expectedHoursView = new ExpectedHoursView(expectedHours);

        return new ViewObjectResponse<>(expectedHoursView);
    }


    @ExceptionHandler(InvalidExpectedHourDatesEx.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ViewObjectErrorResponse handleInvalidExpectedHoursEx(InvalidExpectedHourDatesEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.INVALID_ARGUMENTS, new DateRangeView(ex.getDateRange()));
    }

}
