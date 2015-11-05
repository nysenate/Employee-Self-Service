package gov.nysenate.ess.web.controller.rest;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.client.view.HolidayView;
import gov.nysenate.ess.core.model.period.Holiday;
import gov.nysenate.ess.core.service.period.HolidayService;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.web.client.response.base.BaseResponse;
import gov.nysenate.ess.web.client.response.base.ListViewResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(BaseRestCtrl.REST_PATH + "/holidays")
public class HolidayRestCtrl extends BaseRestCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(HolidayRestCtrl.class);

    @Autowired private HolidayService holidayService;

    @RequestMapping(value = "", params = "year")
    public BaseResponse getHolidaysByYear(@RequestParam Integer year, WebRequest request) {
        LocalDate fromDate = LocalDate.of(year, 1, 1);
        LocalDate toDate = LocalDate.of(year, 12, 31);
        return getListViewResponse(getHolidaysDuring(fromDate, toDate, request));
    }

    @RequestMapping(value = "", params = {"fromDate", "toDate"})
    public BaseResponse getHolidays(@RequestParam String fromDate, @RequestParam String toDate, WebRequest request) {
        LocalDate fromLocalDate = parseISODate(fromDate, "from-date");
        LocalDate toLocalDate = parseISODate(toDate, "to-date");
        return getListViewResponse(getHolidaysDuring(fromLocalDate, toLocalDate, request));
    }

    private List<Holiday> getHolidaysDuring(LocalDate fromDate, LocalDate toDate, WebRequest request) {
        return holidayService.getHolidays(Range.closed(fromDate, toDate), false, SortOrder.ASC);
    }

    private BaseResponse getListViewResponse(List<Holiday> holidays) {
        return ListViewResponse.of(
                holidays.stream().map(HolidayView::new).collect(toList()), "holidays");
    }
}