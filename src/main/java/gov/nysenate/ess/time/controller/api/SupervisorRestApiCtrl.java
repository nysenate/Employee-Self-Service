package gov.nysenate.ess.time.controller.api;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.time.client.view.personnel.*;
import gov.nysenate.ess.time.model.auth.EssTimePermission;
import gov.nysenate.ess.time.model.personnel.SupOverrideType;
import gov.nysenate.ess.time.model.personnel.SupervisorChain;
import gov.nysenate.ess.time.model.personnel.SupervisorMissingEmpsEx;
import gov.nysenate.ess.time.model.personnel.SupervisorOverride;
import gov.nysenate.ess.time.service.personnel.SupervisorInfoService;
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

import static gov.nysenate.ess.time.model.auth.TimePermissionObject.*;
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
                                               @RequestParam(required = false) String toDate,
                                               @RequestParam(defaultValue = "false") boolean extended) {
        LocalDate fromLocalDate = Optional.ofNullable(fromDate)
                .map(dateString -> parseISODate(dateString, "fromDate"))
                .orElse(DateUtils.LONG_AGO);
        LocalDate toLocalDate = Optional.ofNullable(toDate)
                .map(dateString -> parseISODate(dateString, "toDate"))
                .orElse(DateUtils.THE_FUTURE);

        Range<LocalDate> dateRange =
                getClosedOpenRange(fromLocalDate, toLocalDate, "fromDate", "toDate");

        checkPermission(new EssTimePermission(supId, SUPERVISOR_EMPLOYEES, GET,
                Range.closed(fromLocalDate, toLocalDate)));

        ViewObject responseData = extended
                ? new ExtendedSupEmpGroupView(supInfoService.getExtendedSupEmpGroup(supId, dateRange))
                : new SupervisorEmpGroupView(supInfoService.getSupervisorEmpGroup(supId, dateRange));

        return new ViewObjectResponse<>(responseData);
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
                .map(ovr -> new SupervisorOverrideView(ovr, empInfoService.getEmployee(ovr.getGranterEmpId())))
                .collect(toList()), "overrides");
    }

    @RequestMapping(value = "/grants", method = GET)
    public BaseResponse getSupervisorGrants(@RequestParam Integer supId) {
        checkPermission(new EssTimePermission(supId, SUPERVISOR_OVERRIDES, GET, LocalDate.now()));

        List<SupervisorOverride> overrides = supInfoService.getSupervisorGrants(supId);
        return ListViewResponse.of(overrides.stream()
                .filter(ovr -> SupOverrideType.SUPERVISOR == ovr.getSupOverrideType())
                .map(ovr -> new SupervisorGrantView(ovr, empInfoService.getEmployee(ovr.getGranteeEmpId())))
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
            override.setGranteeEmpId(grantView.getGranteeSupervisorId());
            override.setGranterEmpId(grantView.getGranterSupervisorId());
            override.setStartDate(grantView.getStartDate());
            override.setEndDate(grantView.getEndDate());
            override.setActive(grantView.isActive());
            override.setSupOverrideType(SupOverrideType.SUPERVISOR);
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