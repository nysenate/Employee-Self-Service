package gov.nysenate.ess.time.controller.api;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.view.base.BigDecimalView;
import gov.nysenate.ess.core.client.view.base.ListView;
import gov.nysenate.ess.core.client.view.base.MapView;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.time.dao.accrual.DonationDao;
import gov.nysenate.ess.time.model.auth.EssTimePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static gov.nysenate.ess.time.model.auth.TimePermissionObject.ACCRUAL;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/donation")
public class DonationCtrl extends BaseRestApiCtrl {
    private final DonationDao donationDao;

    @Autowired
    public DonationCtrl(DonationDao donationDao) {
        this.donationDao = donationDao;
    }

    @GetMapping("/timeDonatedInPastYear")
    public BaseResponse getTimeDonatedInPastYear(@RequestParam int empId, @RequestParam String effectiveDate) {
        checkPermission(new EssTimePermission(empId, ACCRUAL, GET, Range.singleton(LocalDate.now())));
        LocalDate date = LocalDate.parse(effectiveDate.split("T")[0]);
        var result = donationDao.getTimeDonatedInLastYear(empId, date);
        return new ViewObjectResponse<>(new BigDecimalView(result));
    }

    @GetMapping("/history")
    public BaseResponse getDonationHistory(@RequestParam int empId) {
        var tempMap = new TreeMap<Integer, List<String>>();
        // All possible years should be included.
        for (int year = 2023; year <= LocalDate.now().getYear(); year++) {
            tempMap.put(year, new ArrayList<>());
        }
        // Strings should be ordered by date.
        for (var entry : donationDao.getAllDonatedTime(empId).entries().stream().sorted().collect(Collectors.toList())) {
            tempMap.get(entry.getKey().getYear())
                    .add(donationString(entry.getKey(), entry.getValue()));
        }
        var viewMap = new HashMap<Integer, ListView<String>>();
        tempMap.forEach((year, donation) -> viewMap.put(year, ListView.ofStringList(donation)));
        return new ViewObjectResponse<>(MapView.of(viewMap));
    }

    @PostMapping("/submit")
    // TODO: finish
    public BaseResponse submitDonation(@RequestParam int empId, @RequestParam String effectiveDate, @RequestParam String donation) {
        checkPermission(new EssTimePermission(empId, ACCRUAL, POST, Range.singleton(LocalDate.now())));
        return null;
    }

    private static String donationString(LocalDate date, Float hours) {
        return date.getMonthValue() + "/" + date.getDayOfMonth() + ": " + hours;
    }
}
