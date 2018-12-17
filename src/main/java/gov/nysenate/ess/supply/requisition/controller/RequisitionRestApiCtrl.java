package gov.nysenate.ess.supply.requisition.controller;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.response.error.ErrorResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.dao.unit.LocationDao;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.util.OrderBy;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.supply.authorization.permission.RequisitionPermission;
import gov.nysenate.ess.supply.authorization.permission.SupplyPermission;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.view.LineItemView;
import gov.nysenate.ess.supply.requisition.model.*;
import gov.nysenate.ess.supply.requisition.exception.ConcurrentRequisitionUpdateException;
import gov.nysenate.ess.supply.requisition.service.RequisitionService;
import gov.nysenate.ess.supply.requisition.view.RequisitionView;
import gov.nysenate.ess.supply.requisition.view.SubmitRequisitionView;
import gov.nysenate.ess.supply.socket.RequisitionUpdateEvent;
import org.apache.shiro.authz.UnauthorizedException;
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
    @Autowired private LocationDao locationDao;

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse submitRequisition(@RequestBody SubmitRequisitionView submitRequisitionView) {
        Set<LineItem> lineItems = new HashSet<>();
        for (LineItemView lineItemView : submitRequisitionView.getLineItems()) {
            lineItems.add(lineItemView.toLineItem());
        }
        Requisition requisition = new Requisition.Builder()
                .withCustomer(employeeService.getEmployee(submitRequisitionView.getCustomerId()))
                .withDestination(locationDao.getLocation(new LocationId(submitRequisitionView.getDestinationId())))
                .withDeliveryMethod(DeliveryMethod.valueOf(submitRequisitionView.getDeliveryMethod()))
                .withLineItems(lineItems)
                .withSpecialInstructions(submitRequisitionView.getSpecialInstructions())
                .withState(new PendingState())
                .withModifiedBy(employeeService.getEmployee(submitRequisitionView.getCustomerId()))
                .withOrderedDateTime(LocalDateTime.now())
                .build();
        Requisition savedRequisition = requisitionService.submitRequisition(requisition);
        RequisitionView requisitionView = new RequisitionView(savedRequisition);
        eventBus.post(new RequisitionUpdateEvent(requisitionView));
        return new ViewObjectResponse<>(requisitionView);
    }

    @RequestMapping("/{id}")
    public BaseResponse getRequisitionById(@PathVariable int id) {
        Requisition requisition = requisitionService.getRequisitionById(id).orElse(null);
        checkViewRequisitionPermissions(requisition);
        return new ViewObjectResponse<>(new RequisitionView(requisition));
    }

    /**
     * Saves changes made to a requisition without processing it to the next state.
     * @param id
     * @param requisitionView
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void saveRequisition(@PathVariable int id, @RequestBody RequisitionView requisitionView) {
        Requisition requisition = requisitionView.toRequisition();
        checkPermission(RequisitionPermission.forCustomer(requisition.getCustomer().getEmployeeId(), RequestMethod.POST));

        requisition = requisition.setModifiedBy(getModifiedBy());
        requisition = requisitionService.saveRequisition(requisition);
        eventBus.post(new RequisitionUpdateEvent(new RequisitionView(requisition)));
    }

    /**
     * Process requisition and save any changes made.
     * @param id
     * @param requisitionView
     */
    @RequestMapping(value = "/{id}/process", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void processRequisition(@PathVariable int id, @RequestBody RequisitionView requisitionView) {
        Requisition requisition = requisitionView.toRequisition();
        checkPermission(RequisitionPermission.forCustomer(requisition.getCustomer().getEmployeeId(), RequestMethod.POST));

        if (requisition.getStatus() == RequisitionStatus.COMPLETED) {
            checkPermission(SupplyPermission.SUPPLY_REQUISITION_APPROVE.getPermission());
        }
        if (!requisition.getIssuer().isPresent()) {
            requisition = requisition.setIssuer(getModifiedBy());
        }
        requisition = requisition.setModifiedBy(getModifiedBy());
        requisition = requisitionService.processRequisition(requisition);
        eventBus.post(new RequisitionUpdateEvent(new RequisitionView(requisition)));
    }

    /**
     * Reject a requisition.
     * @param id
     * @param requisitionView
     */
    @RequestMapping(value = "/{id}/reject", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void rejectRequisition(@PathVariable int id, @RequestBody RequisitionView requisitionView) {
        Requisition requisition = requisitionView.toRequisition();
        checkPermission(RequisitionPermission.forCustomer(requisition.getCustomer().getEmployeeId(), RequestMethod.POST));

        requisition = requisition.setModifiedBy(getModifiedBy());
        requisition = requisitionService.rejectRequisition(requisition);
        eventBus.post(new RequisitionUpdateEvent(new RequisitionView(requisition)));
    }

    /**
     * Searches for requisitions, returning all that match the given parameters.
     * Only include parameters you wish to filter for.
     * This endpoint is restricted to supply employees.
     *
     *      GET: /api/v1/supply/requisitions.json
     *
     * Optional Params:
     *      location: string - A location id string matching a requisitions destination, e.g A42FB-W.
     *      customerId: string - Searches for requisitions ordered by this employee id.
     *      status: string[] - Searches for requisitions with one of the given {@code RequisitionStatuses}.
     *      from: string - An ISO date to search from e.g. "2017-08-18T23:59:59"
     *      to: string - An ISO date to search to. e.g. "2017-08-18T23:59:59"
     *      issueId: string - Searches for requisitions issued by this employee id.
     *      dateField: string - The field to filter by with {@code from} and {@code to}.
     *                  Must be one of: "ordered_date_time", "processed_date_time", "completed_date_time",
     *                                  "approved_date_time", "rejected_date_time"
     *      savedInSfms: string - Searches for requisitions based on if they are saved in sfms.
     *                  Must be one of: "true", "false"
     *      itemId: string - Searches for requisitions containing this item id.
     */
    // TODO: remove 'All' params, if we want all for a param we should not send it and it will not filter by that param.
    @RequestMapping("")
    public BaseResponse searchRequisitions(@RequestParam(defaultValue = "All", required = false) String location,
                                           @RequestParam(defaultValue = "All", required = false) String customerId,
                                           @RequestParam(required = false) String[] status,
                                           @RequestParam(required = false) String from,
                                           @RequestParam(required = false) String to,
                                           @RequestParam(defaultValue = "All", required = false) String issuerId,
                                           @RequestParam(required = false) String dateField,
                                           @RequestParam(defaultValue = "All", required = false) String savedInSfms,
                                           @RequestParam(defaultValue = "All", required = false) String itemId,
                                           @RequestParam(required = false) String reconciled,
                                           WebRequest webRequest) {
        checkPermission(RequisitionPermission.forAll(RequestMethod.GET));

        dateField = dateField == null ? "ordered_date_time" : dateField;
        RequisitionQuery query = new RequisitionQuery()
                .setDestination(location)
                .setCustomerId(customerId)
                .setStatuses(getStatusEnumSet(status))
                .setFromDateTime(getFromDateTime(from))
                .setToDateTime(getToDateTime(to))
                .setDateField(dateField)
                .setSavedInSfms(savedInSfms)
                .setIssuerId(issuerId)
                .setItemId(itemId)
                .setReconciled(reconciled)
                .setLimitOffset(getLimitOffset(webRequest, 25))
                .setOrderBy(new OrderBy(dateField, SortOrder.DESC));


        PaginatedList<Requisition> results = requisitionService.searchRequisitions(query);
        List<RequisitionView> resultViews = results.getResults().stream()
                                                   .map(RequisitionView::new)
                                                   .collect(Collectors.toList());
        return ListViewResponse.of(resultViews, results.getTotal(), results.getLimOff());
    }

    /**
     * Returns a collections of requisitions ordered by an employee or with a specified destination.
     *
     *      GET: /api/v1/supply/orderHistory.json
     *
     * Required Params:
     *      location: string - The locationId of the destination to search for. e.g. "A42FB-W"
     *      customerId: int - The employeeId of the customer to search for.
     *
     * Optional Params:
     *      status: string[] - Only return requisitions with a requisition status of one of these.
     *      from: string - An ISO date string representing the from date time to search from.
     *      to: string - An ISO date string representing the to date time to search to.
     *      dateField: string - The date field to filter by with {@code from} and {@code to}.
     *                  Must be one of: "ordered_date_time", "processed_date_time", "completed_date_time",
     *                                  "approved_date_time", "rejected_date_time"
     */
    @RequestMapping("/orderHistory")
    public BaseResponse orderHistory(@RequestParam String location,
                                     @RequestParam int customerId,
                                     @RequestParam(required = false) String[] status,
                                     @RequestParam(required = false) String from,
                                     @RequestParam(required = false) String to,
                                     @RequestParam(required = false) String dateField,
                                     WebRequest webRequest) {
        if (!getSubject().isPermitted(RequisitionPermission.forCustomer(customerId, RequestMethod.GET))
                || !getSubject().isPermitted(RequisitionPermission.forDestination(location, RequestMethod.GET))) {
            throw new UnauthorizedException();
        }

        dateField = dateField == null ? "ordered_date_time" : dateField;
        RequisitionQuery query = new RequisitionQuery()
                .setDestination(location)
                .setCustomerId(customerId)
                .setStatuses(getStatusEnumSet(status))
                .setFromDateTime(getFromDateTime(from))
                .setToDateTime(getToDateTime(to))
                .setDateField(dateField)
                .setLimitOffset(getLimitOffset(webRequest, 25))
                .setOrderBy(new OrderBy(dateField, SortOrder.DESC));
        PaginatedList<Requisition> results = requisitionService.searchOrderHistory(query);
        List<RequisitionView> resultViews = results.getResults().stream()
                                                   .map(RequisitionView::new)
                                                   .collect(Collectors.toList());
        return ListViewResponse.of(resultViews, results.getTotal(), results.getLimOff());
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
        if (!getSubject().isPermitted(RequisitionPermission.forCustomer(requisition.getCustomer().getEmployeeId(), RequestMethod.GET))
                && !getSubject().isPermitted(RequisitionPermission.forDestination(requisition.getDestination().getLocId().toString(), RequestMethod.GET))) {
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

    private Employee getModifiedBy() {
        return employeeService.getEmployee(getSubjectEmployeeId());
    }
}
