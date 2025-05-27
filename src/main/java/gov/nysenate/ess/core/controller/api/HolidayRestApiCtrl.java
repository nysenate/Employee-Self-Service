package gov.nysenate.ess.core.controller.api;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.view.HolidayView;
import gov.nysenate.ess.core.model.period.Holiday;
import gov.nysenate.ess.core.service.period.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/holidays")
public class HolidayRestApiCtrl extends BaseRestApiCtrl
{
    @Autowired private HolidayService holidayService;

    @RequestMapping(value = "", params = "year")
    public BaseResponse getHolidaysByYear(@RequestParam Integer year) {
        LocalDate fromDate = LocalDate.of(year, 1, 1);
        LocalDate toDate = LocalDate.of(year, 12, 31);
        return getListViewResponse(getHolidaysDuring(fromDate, toDate));
    }

    @RequestMapping(value = "", params = {"fromDate", "toDate"})
    public BaseResponse getHolidays(@RequestParam String fromDate, @RequestParam String toDate) {
        LocalDate fromLocalDate = parseISODate(fromDate, "from-date");
        LocalDate toLocalDate = parseISODate(toDate, "to-date");
        return getListViewResponse(getHolidaysDuring(fromLocalDate, toLocalDate));
    }

    private List<Holiday> getHolidaysDuring(LocalDate fromDate, LocalDate toDate) {
        List<Holiday> holidays = holidayService.getHolidays(fromDate, toDate, false);
        holidays.sort(Comparator.comparing(Holiday::getDate));
        return holidays;
    }

    private BaseResponse getListViewResponse(List<Holiday> holidays) {
        return ListViewResponse.of(
                holidays.stream().map(HolidayView::new).collect(toList()), "holidays");
    }
}