package gov.nysenate.ess.travel.delegate;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.travel.authorization.permission.SimpleTravelPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/delegation")
public class DelegateCtrl extends BaseRestApiCtrl {

    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private DelegationDao delegateDao;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public BaseResponse delegationsForPrincipal(@RequestParam int principalId) {
        checkPermission(SimpleTravelPermission.TRAVEL_ASSIGN_DELEGATES.getPermission());

        List<Delegation> delegations = delegateDao.findByPrincipalEmpId(principalId);
        return delegateListResponse(delegations);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public BaseResponse saveDelegates(@RequestBody List<DelegationView> delegationViews) {
        checkPermission(SimpleTravelPermission.TRAVEL_ASSIGN_DELEGATES.getPermission());

        List<Delegation> delegations = new ArrayList<>();
        Employee principal = employeeInfoService.getEmployee(getSubjectEmployeeId());
        for (DelegationView view : delegationViews) {
            // If no delegate employee was assigned ignore it.
            if (view.delegate == null) {
                continue;
            }
            int id = view.id;
            Employee delegate = employeeInfoService.getEmployee(view.delegate.getEmpId());
            LocalDate startDate = view.useStartDate ? view.startDate() : LocalDate.now();
            LocalDate endDate = view.useEndDate ? view.endDate() : DateUtils.THE_FUTURE;
            delegations.add(new Delegation(id, principal, delegate, startDate, endDate));
        }
        delegateDao.save(delegations, principal.getEmployeeId());
        return delegateListResponse(delegations);
    }

    private ListViewResponse<DelegationView> delegateListResponse(List<Delegation> delegations) {
        return ListViewResponse.of(delegations.stream().map(DelegationView::new).collect(Collectors.toList()));
    }
}
