package gov.nysenate.ess.time.controller.api;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.time.client.view.attendance.MiscLeaveGrantView;
import gov.nysenate.ess.time.client.view.attendance.MiscLeaveGrantWithHoursRemaining;
import gov.nysenate.ess.time.dao.attendance.TimeRecordDao;
import gov.nysenate.ess.time.dao.payroll.MiscLeaveDao;
import gov.nysenate.ess.time.model.attendance.TimeEntry;
import gov.nysenate.ess.time.model.auth.EssTimePermission;
import gov.nysenate.ess.time.model.payroll.MiscLeaveGrant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static gov.nysenate.ess.time.model.auth.TimePermissionObject.MISC_LEAVE_GRANT;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/miscleave")
public class MiscLeaveRestApiCtrl extends BaseRestApiCtrl {
    private final MiscLeaveDao miscLeaveDao;
    private final TimeRecordDao timeRecordDao;

    @Autowired
    public MiscLeaveRestApiCtrl(MiscLeaveDao miscLeaveDao, TimeRecordDao timeRecordDao) {
        this.miscLeaveDao = miscLeaveDao;
        this.timeRecordDao = timeRecordDao;
    }

    @RequestMapping("/grants")
    public BaseResponse getMiscLeaveGrants(@RequestParam int empId) {
        checkPermission(new EssTimePermission(empId, MISC_LEAVE_GRANT, GET, Range.all()));
        List<MiscLeaveGrant> grantList = miscLeaveDao.getMiscLeaveGrants(empId);
        return ListViewResponse.of(grantList.stream()
                .map(MiscLeaveGrantView::new).collect(Collectors.toList()));
    }

    @RequestMapping("/grantsWithRemainingHours")
    public BaseResponse getMiscLeaveGrantWithRemainingHours(@RequestParam int empId, @RequestParam String endDateStr) {
        LocalDate endDate = LocalDate.parse(endDateStr);
        Map<MiscLeaveGrant, BigDecimal> grantHourMap = setRemainingHours(empId, endDate);
        return ListViewResponse.of(grantHourMap.entrySet().stream()
                .map(entry -> new MiscLeaveGrantWithHoursRemaining(new MiscLeaveGrantView(entry.getKey()), entry.getValue()))
                .collect(Collectors.toList()));
    }

    private Map<MiscLeaveGrant, BigDecimal> setRemainingHours(int empId, LocalDate endDate) {
        List<MiscLeaveGrant> grants = miscLeaveDao.getMiscLeaveGrants(empId);
        LocalDate beginDate = grants.stream().map(MiscLeaveGrant::beginDate)
                .min(Comparator.naturalOrder()).orElse(null);
        var tempGrantHoursMap = new HashMap<MiscLeaveGrant, BigDecimal>();
        if (beginDate == null) {
            return tempGrantHoursMap;
        }

        List<TimeEntry> miscEntries;
        try {
            Range<LocalDate> dateRange = Range.closedOpen(beginDate, endDate);
            miscEntries = timeRecordDao.getRecordsDuring(empId, dateRange).stream()
                    .flatMap(timeRecord -> timeRecord.getTimeEntries().stream())
                    .collect(Collectors.toList());
        }
        // This exception means the end date is less than the beginning date, so no entries fall in this range.
        catch (IllegalArgumentException ex) {
            miscEntries = List.of();
        }

        for (MiscLeaveGrant grant : grants) {
            if (grant.hours() == null) {
                tempGrantHoursMap.put(grant, null);
                continue;
            }
            BigDecimal tempGrantHours = grant.hours();
            for (TimeEntry miscEntry : miscEntries) {
                if (miscEntry.getMiscHours().isPresent()
                        && miscEntry.getMiscType() == grant.miscLeaveType()
                        && grant.getDateRange().contains(miscEntry.getDate())) {
                    tempGrantHours = tempGrantHours.subtract(miscEntry.getMiscHours().get());
                }
            }
            tempGrantHoursMap.put(grant, tempGrantHours);
        }
        return tempGrantHoursMap;
    }
}
