package gov.nysenate.ess.travel.delegate;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/delegate")
public class DelegateCtrl extends BaseRestApiCtrl {

    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private DelegateDao delegateDao;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public BaseResponse searchDelegate(@RequestParam int principalId) {
        // TODO Check permissions
        List<Delegate> delegates = delegateDao.activeDelegates(principalId, LocalDate.now());
        return delegateListResponse(delegates);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public BaseResponse saveDelegates(@RequestBody List<DelegateView> delegateViews) {
        // TODO Check permissions
        List<Delegate> delegates = new ArrayList<>();
        Employee principal = employeeInfoService.getEmployee(getSubjectEmployeeId());
        for (DelegateView view : delegateViews) {
            // If no delegate employee was assigned ignore it.
            if (view.delegate == null) {
                continue;
            }
            int id = view.id;
            Employee delegate = employeeInfoService.getEmployee(view.delegate.getEmpId());
            LocalDate startDate = view.useStartDate ? view.startDate() : LocalDate.now();
            LocalDate endDate = view.useEndDate ? view.endDate() : DateUtils.THE_FUTURE;
            delegates.add(new Delegate(id, principal, delegate, startDate, endDate));
        }

        delegateDao.saveDelegates(delegates);

        return delegateListResponse(delegates);
    }

    private ListViewResponse<DelegateView> delegateListResponse(List<Delegate> delegates) {
        return ListViewResponse.of(delegates.stream().map(DelegateView::new).collect(Collectors.toList()));
    }
}
