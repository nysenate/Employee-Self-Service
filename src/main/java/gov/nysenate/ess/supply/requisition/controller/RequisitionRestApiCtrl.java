package gov.nysenate.ess.supply.requisition.controller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.response.error.ErrorResponse;
import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.auth.SenatePerson;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.unit.LocationService;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.view.LineItemView;
import gov.nysenate.ess.supply.requisition.model.Requisition;
import gov.nysenate.ess.supply.requisition.model.RequisitionStatus;
import gov.nysenate.ess.supply.requisition.exception.ConcurrentRequisitionUpdateException;
import gov.nysenate.ess.supply.requisition.service.RequisitionService;
import gov.nysenate.ess.supply.requisition.view.RequisitionView;
import gov.nysenate.ess.supply.requisition.view.SubmitRequisitionView;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supply/requisitions")
public class RequisitionRestApiCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(RequisitionRestApiCtrl.class);

    @Autowired private RequisitionService requisitionService;
    @Autowired private EmployeeInfoService employeeService;
    @Autowired private LocationService locationService;

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse submitRequisition(@RequestBody SubmitRequisitionView submitRequisitionView) {
        Set<LineItem> lineItems = new HashSet<>();
        for (LineItemView lineItemView : submitRequisitionView.getLineItems()) {
            lineItems.add(lineItemView.toLineItem());
        }
        Requisition requisition = new Requisition.Builder()
                .withCustomer(employeeService.getEmployee(submitRequisitionView.getCustomerId()))
                .withDestination(locationService.getLocation(new LocationId(submitRequisitionView.getDestinationId())))
                .withLineItems(lineItems)
                .withSpecialInstructions(submitRequisitionView.getSpecialInstructions())
                .withStatus(RequisitionStatus.PENDING)
                .withModifiedBy(employeeService.getEmployee(submitRequisitionView.getCustomerId()))
                .withOrderedDateTime(LocalDateTime.now())
                .build();
        Requisition savedRequisition = requisitionService.saveRequisition(requisition);
        return new ViewObjectResponse<>(new RequisitionView(savedRequisition));
    }

    @RequestMapping("/{id}")
    public BaseResponse getRequisitionById(@PathVariable int id) {
        Requisition requisition = requisitionService.getRequisitionById(id).orElse(null);
        checkViewRequisitionPermissions(requisition);
        return new ViewObjectResponse<>(new RequisitionView(requisition));
    }

    @RequestMapping("")
    public BaseResponse searchRequisitions(@RequestParam(defaultValue = "All", required = false) String location,
                                           @RequestParam(defaultValue = "All", required = false) String customerId,
                                           @RequestParam(required = false) String[] status,
                                           @RequestParam(required = false) String from,
                                           @RequestParam(required = false) String to,
                                           @RequestParam(defaultValue = "All", required = false) String issuerId,
                                           @RequestParam(required = false) String dateField,
                                           @RequestParam(defaultValue = "All", required = false) String savedInSfms,
                                           WebRequest webRequest) {
        checkPermission(new WildcardPermission("supply:employee"));
        LocalDateTime fromDateTime = getFromDateTime(from);
        LocalDateTime toDateTime = getToDateTime(to);
        EnumSet<RequisitionStatus> statuses = getStatusEnumSet(status);
        dateField = dateField == null ? "ordered_date_time" : dateField;

        LimitOffset limoff = getLimitOffset(webRequest, 25);
        Range<LocalDateTime> dateRange = getClosedRange(fromDateTime, toDateTime, "from", "to");
        PaginatedList<Requisition> results;
        results = requisitionService.searchRequisitions(location, customerId, statuses, dateRange, dateField, savedInSfms, limoff, issuerId);
        List<RequisitionView> resultViews = results.getResults().stream()
                                                   .map(RequisitionView::new)
                                                   .collect(Collectors.toList());
        return ListViewResponse.of(resultViews, results.getTotal(), results.getLimOff());
    }

    @RequestMapping("/orderHistory")
    public BaseResponse orderHistory(@RequestParam String location,
                                     @RequestParam int customerId,
                                     @RequestParam(required = false) String[] status,
                                     @RequestParam(required = false) String from,
                                     @RequestParam(required = false) String to,
                                     @RequestParam(required = false) String dateField,
                                     WebRequest webRequest) {
        if (!getSubject().isPermitted("supply:requisition:view:customer:" + String.valueOf(customerId))
                || !getSubject().isPermitted("supply:requisition:view:destination:" + location)) {
            throw new UnauthorizedException();
        }

        LocalDateTime fromDateTime = getFromDateTime(from);
        LocalDateTime toDateTime = getToDateTime(to);
        EnumSet<RequisitionStatus> statuses = getStatusEnumSet(status);
        dateField = dateField == null ? "ordered_date_time" : dateField;
        LimitOffset limoff = getLimitOffset(webRequest, 25);

        Range<LocalDateTime> dateRange = getClosedRange(fromDateTime, toDateTime, "from", "to");
        PaginatedList<Requisition> results = requisitionService.searchOrderHistory(location, customerId, statuses, dateRange, dateField, limoff);
        List<RequisitionView> resultViews = results.getResults().stream()
                                                   .map(RequisitionView::new)
                                                   .collect(Collectors.toList());
        return ListViewResponse.of(resultViews, results.getTotal(), results.getLimOff());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void saveRequisition(@PathVariable int id, @RequestBody RequisitionView requisitionView) {

        checkPermission(new WildcardPermission("supply:requisition:edit"));
        if (RequisitionStatus.valueOf(requisitionView.getStatus()) == RequisitionStatus.APPROVED) {
            checkPermission(new WildcardPermission("supply:requisition:approve"));
        }

        Requisition requisition = requisitionView.toRequisition();
        requisition = requisition.setModifiedBy(getSubjectEmployeeView().toEmployee());
        requisitionService.saveRequisition(requisition);
    }

    /**
     * Returns a set of requisition objects which represent the history of a requisition.
     * Each change made to the requisition is contained in a separate requisition object.
     */
    @RequestMapping(value = "/history/{id}")
    public BaseResponse requisitionHistory(@PathVariable int id) {
        ImmutableList<Requisition> requisitions = requisitionService.getRequisitionHistory(id);
        // Only check permissions for the current version/revision.
        checkViewRequisitionPermissions(requisitions.get(requisitions.size() - 1));
        return ListViewResponse.of(requisitions.stream().map(RequisitionView::new).collect(Collectors.toList()));
    }

    /**
     * Checks that a user can view an individual requisition.
     * User can view if they are the customer or the requisition destination
     * is the user's work location.
     * @param requisition The requisition being requested.
     */
    private void checkViewRequisitionPermissions(Requisition requisition) {
        if (!getSubject().isPermitted("supply:requisition:view:customer:" + String.valueOf(requisition.getCustomer().getEmployeeId()))
                && !getSubject().isPermitted("supply:requisition:view:destination:" + requisition.getDestination().getLocId().toString())) {
            throw new UnauthorizedException();
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConcurrentRequisitionUpdateException.class)
    @ResponseBody
    public ErrorResponse handleConcurrentRequisitionUpdate(ConcurrentRequisitionUpdateException ex) {
        return new ErrorResponse(ErrorCode.REQUISITION_UPDATE_CONFLICT);
    }

    /**
     * @return the LocalDateTime represented by {@code from} or a LocalDateTime from one month ago if from is null.
     */
    private LocalDateTime getFromDateTime(String from) {
        return from == null ? LocalDateTime.now().minusMonths(1) : parseISODateTime(from, "from");
    }

    /**
     * @return the LocalDateTime represented by {@code to} or the current LocalDateTime.
     */
    private LocalDateTime getToDateTime(@RequestParam(required = false) String to) {
        return to == null ? LocalDateTime.now() : parseISODateTime(to, "to");
    }

    /**
     * @param status An array of strings each representing a {@link RequisitionStatus}.
     * @return An enumset of the given statuses or an enumset of all RequisitionStatuses if status is null.
     */
    private EnumSet<RequisitionStatus> getStatusEnumSet(String[] status) {
        return status == null ? EnumSet.allOf(RequisitionStatus.class) : getEnumSetFromStringArray(status);
    }

    private EnumSet<RequisitionStatus> getEnumSetFromStringArray(String[] status) {
        List<RequisitionStatus> statusList = new ArrayList<>();
        for (String s : status) {
            statusList.add(RequisitionStatus.valueOf(s));
        }
        return EnumSet.copyOf(statusList);
    }

    private EmployeeView getSubjectEmployeeView() {
        return new EmployeeView(getModifiedBy());
    }

    private Employee getModifiedBy() {
        return employeeService.getEmployee(getSubjectEmployeeId());
    }

    private int getSubjectEmployeeId() {
        SenatePerson person = (SenatePerson) getSubject().getPrincipals().getPrimaryPrincipal();
        return person.getEmployeeId();
    }
}
