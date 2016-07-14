package gov.nysenate.ess.seta.controller.api;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.seta.client.view.*;
import gov.nysenate.ess.seta.model.auth.EssTimePermission;
import gov.nysenate.ess.seta.model.personnel.SupervisorChain;
import gov.nysenate.ess.seta.model.personnel.SupervisorException;
import gov.nysenate.ess.seta.model.personnel.SupervisorMissingEmpsEx;
import gov.nysenate.ess.seta.model.personnel.SupervisorOverride;
import gov.nysenate.ess.seta.service.personnel.SupervisorInfoService;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.response.error.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gov.nysenate.ess.seta.model.auth.TimePermissionObject.*;
import static java.util.stream.Collectors.toList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supervisor")
public class SupervisorRestApiCtrl extends BaseRestApiCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(SupervisorRestApiCtrl.class);

    @Autowired private EmployeeInfoService empInfoService;
    @Autowired private SupervisorInfoService supInfoService;

    @RequestMapping(value = "/employees")
    public BaseResponse getSupervisorEmployees(@RequestParam Integer supId,
                                               @RequestParam(required = false) String fromDate,
                                               @RequestParam(required = false) String toDate) {
        LocalDate fromLocalDate = (fromDate != null) ? parseISODate(fromDate, "from date") : DateUtils.LONG_AGO;
        LocalDate toLocalDate = (toDate != null) ? parseISODate(toDate, "to date") : DateUtils.THE_FUTURE;

        checkPermission(new EssTimePermission(supId, SUPERVISOR_EMPLOYEES, GET,
                Range.closed(fromLocalDate, toLocalDate)));

        return new ViewObjectResponse<>(
            new SupervisorEmpGroupView(
                supInfoService.getSupervisorEmpGroup(supId, Range.closed(fromLocalDate, toLocalDate))));
    }

    @RequestMapping(value = "/chain")
    public BaseResponse getSupervisorChain(@RequestParam Integer empId,
                                           @RequestParam(required = false) String date) {
        LocalDate localDate = (date != null) ? parseISODate(date, "date") : LocalDate.now();

        checkPermission(new EssTimePermission(empId, SUPERVISOR, GET, localDate));

        SupervisorChain supervisorChain = supInfoService.getSupervisorChain(empId, localDate, 3);
        Map<Integer, Employee> empMap = supervisorChain.getChain().stream()
                .map(empInfoService::getEmployee)
                .collect(Collectors.toMap(Employee::getEmployeeId, Function.identity()));
        return new ViewObjectResponse<>(
            new SupervisorChainView(supervisorChain, empMap)
        );
    }

    @RequestMapping(value = "/overrides")
    public BaseResponse getSupervisorOverrides(@RequestParam Integer supId) {
        checkPermission(new EssTimePermission(supId, SUPERVISOR_OVERRIDES, GET, LocalDate.now()));

        List<SupervisorOverride> overrides = supInfoService.getSupervisorOverrides(supId);
        return ListViewResponse.of(overrides.stream()
                .map(ovr -> new SupervisorOverrideView(ovr, empInfoService.getEmployee(ovr.getGranterSupervisorId())))
                .collect(toList()), "overrides");
    }

    @RequestMapping(value = "/grants", method = GET)
    public BaseResponse getSupervisorGrants(@RequestParam Integer supId) {
        checkPermission(new EssTimePermission(supId, SUPERVISOR_OVERRIDES, GET, LocalDate.now()));

        List<SupervisorOverride> overrides = supInfoService.getSupervisorGrants(supId);
        return ListViewResponse.of(overrides.stream()
                .map(ovr -> new SupervisorGrantView(ovr, empInfoService.getEmployee(ovr.getGranteeSupervisorId())))
                .collect(toList()), "grants");
    }

    @RequestMapping(value = "/grants", method = POST, consumes = "application/json")
    public BaseResponse updateSupervisorGrants(@RequestBody SupervisorGrantSimpleView[] grantViews) {
        Set<Integer> supIds = Arrays.stream(grantViews)
                .map(SupervisorGrantSimpleView::getGranterSupervisorId)
                .collect(Collectors.toSet());

        supIds.forEach(supId ->
                checkPermission(new EssTimePermission(supId, SUPERVISOR_OVERRIDES, POST, LocalDate.now())));

        for (SupervisorGrantSimpleView grantView : grantViews) {
            if (grantView == null || grantView.getGranteeSupervisorId() == 0 || grantView.getGranterSupervisorId() == 0) {
                throw new IllegalArgumentException("Grant must contain a valid supervisor and override supervisor id.");
            }
            SupervisorOverride override = new SupervisorOverride();
            override.setGranteeSupervisorId(grantView.getGranteeSupervisorId());
            override.setGranterSupervisorId(grantView.getGranterSupervisorId());
            override.setStartDate(Optional.ofNullable(grantView.getStartDate()));
            override.setEndDate(Optional.ofNullable(grantView.getEndDate()));
            override.setActive(grantView.isActive());
            supInfoService.updateSupervisorOverride(override);
        }
        return new SimpleResponse(true, "Grants have been updated", "update supervisor grant");
    }

    @ExceptionHandler(SupervisorMissingEmpsEx.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleMissingEmpsEx(SupervisorMissingEmpsEx ex) {
        return new ErrorResponse(ErrorCode.EMPLOYEE_NOT_SUPERVISOR, "The employee requested is not a supervisor");
    }
}