package gov.nysenate.ess.web.controller.rest;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.seta.client.view.*;
import gov.nysenate.ess.seta.model.personnel.SupervisorChain;
import gov.nysenate.ess.seta.model.personnel.SupervisorException;
import gov.nysenate.ess.seta.model.personnel.SupervisorOverride;
import gov.nysenate.ess.seta.service.personnel.SupervisorInfoService;
import gov.nysenate.ess.web.client.response.base.BaseResponse;
import gov.nysenate.ess.web.client.response.base.ListViewResponse;
import gov.nysenate.ess.web.client.response.base.SimpleResponse;
import gov.nysenate.ess.web.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.web.client.response.error.ErrorCode;
import gov.nysenate.ess.web.client.response.error.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(BaseRestCtrl.REST_PATH + "/supervisor")
public class SupervisorRestCtrl extends BaseRestCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(SupervisorRestCtrl.class);

    @Autowired private EmployeeInfoService empInfoService;
    @Autowired private SupervisorInfoService supInfoService;

    @RequestMapping(value = "/employees")
    public BaseResponse getSupervisorEmployees(@RequestParam Integer supId,
                                               @RequestParam(required = false) String fromDate,
                                               @RequestParam(required = false) String toDate) {
        LocalDate fromLocalDate = (fromDate != null) ? parseISODate(fromDate, "from date") : DateUtils.LONG_AGO;
        LocalDate toLocalDate = (toDate != null) ? parseISODate(toDate, "to date") : DateUtils.THE_FUTURE;
        try {
            return new ViewObjectResponse<>(
                new SupervisorEmpGroupView(
                    supInfoService.getSupervisorEmpGroup(supId, Range.closed(fromLocalDate, toLocalDate))));
        }
        catch (SupervisorException e) {
            return new ErrorResponse(ErrorCode.APPLICATION_ERROR);
        }
    }

    @RequestMapping(value = "/chain")
    public BaseResponse getSupervisorChain(@RequestParam Integer empId,
                                           @RequestParam(required = false) String date) {
        LocalDate localDate = (date != null) ? parseISODate(date, "date") : LocalDate.now();
        try {
            SupervisorChain supervisorChain = supInfoService.getSupervisorChain(empId, localDate, 3);
            Map<Integer, Employee> empMap = supervisorChain.getChain().stream()
                    .map(empInfoService::getEmployee)
                    .collect(Collectors.toMap(Employee::getEmployeeId, Function.identity()));
            return new ViewObjectResponse<>(
                new SupervisorChainView(supervisorChain, empMap)
            );
        }
        catch (SupervisorException e) {
            return new ErrorResponse(ErrorCode.APPLICATION_ERROR);
        }
    }

    @RequestMapping(value = "/overrides")
    public BaseResponse getSupervisorOverrides(@RequestParam Integer supId) {
        try {
            List<SupervisorOverride> overrides = supInfoService.getSupervisorOverrides(supId);
            return ListViewResponse.of(overrides.stream()
                    .map(ovr -> new SupervisorOverrideView(ovr, empInfoService.getEmployee(ovr.getGranterSupervisorId())))
                    .collect(toList()), "overrides");
        }
        catch (SupervisorException e) {
            return new ErrorResponse(ErrorCode.APPLICATION_ERROR);
        }
    }

    @RequestMapping(value = "/grants", method = RequestMethod.GET)
    public BaseResponse getSupervisorGrants(@RequestParam Integer supId) {
        try {
            List<SupervisorOverride> overrides = supInfoService.getSupervisorGrants(supId);
            return ListViewResponse.of(overrides.stream()
                    .map(ovr -> new SupervisorGrantView(ovr, empInfoService.getEmployee(ovr.getGranteeSupervisorId())))
                    .collect(toList()), "grants");
        }
        catch (SupervisorException e) {
            return new ErrorResponse(ErrorCode.APPLICATION_ERROR);
        }
    }

    @RequestMapping(value = "/grants", method = RequestMethod.POST, consumes = "application/json")
    public BaseResponse updateSupervisorGrants(@RequestBody SupervisorGrantSimpleView[] grantViews) {
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
            try {
                supInfoService.updateSupervisorOverride(override);
            }
            catch (SupervisorException e) {
                return new ErrorResponse(ErrorCode.APPLICATION_ERROR, e.getMessage());
            }
        }
        return new SimpleResponse(true, "Grants have been updated", "update supervisor grant");
    }
}