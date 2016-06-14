package gov.nysenate.ess.supply.requisition.controller;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.auth.SenatePerson;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.unit.LocationService;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.view.LineItemView;
import gov.nysenate.ess.supply.requisition.view.SubmitRequisitionView;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;
import gov.nysenate.ess.supply.requisition.RequisitionVersion;
import gov.nysenate.ess.supply.requisition.service.RequisitionService;
import gov.nysenate.ess.supply.requisition.view.RequisitionVersionView;
import gov.nysenate.ess.supply.requisition.view.RequisitionView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
        Employee customer = employeeService.getEmployee(submitRequisitionView.getCustomerId());
        Location destination = locationService.getLocation(new LocationId(submitRequisitionView.getDestinationId()));
        RequisitionVersion version = new RequisitionVersion.Builder().withCustomer(customer)
                                                                     .withDestination(destination)
                                                                     .withLineItems(lineItems)
                                                                     .withStatus(RequisitionStatus.PENDING)
                                                                     .withCreatedBy(customer)
                                                                     .build();
        Requisition requisition = new Requisition(LocalDateTime.now(), version);
        int id = requisitionService.saveRequisition(requisition);
        return new ViewObjectResponse<>(new RequisitionView(requisitionService.getRequisitionById(id)));
    }

    @RequestMapping("/{id}")
    public BaseResponse getRequisitionById(@PathVariable int id) {
        Requisition requisition = requisitionService.getRequisitionById(id);
        return new ViewObjectResponse<>(new RequisitionView(requisition));
    }

    @RequestMapping("")
    public BaseResponse searchRequisitions(@RequestParam(defaultValue = "all", required = false) String location,
                                           @RequestParam(defaultValue = "all", required = false) String customerId,
                                           @RequestParam(required = false) String[] status,
                                           @RequestParam(required = false) String from,
                                           @RequestParam(required = false) String to,
                                           @RequestParam(required = false) String dateField,
                                           WebRequest webRequest) {
        LocalDateTime fromDateTime = getFromDateTime(from);
        LocalDateTime toDateTime = getToDateTime(to);
        EnumSet<RequisitionStatus> statuses = getStatusEnumSet(status);
        dateField = dateField == null ? "modified_date_time" : dateField;

        LimitOffset limoff = getLimitOffset(webRequest, 25);
        Range<LocalDateTime> dateRange = getClosedRange(fromDateTime, toDateTime, "from", "to");
        PaginatedList<Requisition> results = requisitionService.searchRequisitions(location, customerId, statuses, dateRange, dateField, limoff);
        List<RequisitionView> resultViews = results.getResults().stream().map(RequisitionView::new).collect(Collectors.toList());
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
        LocalDateTime fromDateTime = getFromDateTime(from);
        LocalDateTime toDateTime = getToDateTime(to);
        EnumSet<RequisitionStatus> statuses = getStatusEnumSet(status);
        dateField = dateField == null ? "ordered_date_time" : dateField;
        LimitOffset limoff = getLimitOffset(webRequest, 25);

        Range<LocalDateTime> dateRange = getClosedRange(fromDateTime, toDateTime, "from", "to");
        PaginatedList<Requisition> results = requisitionService.searchOrderHistory(location, customerId, statuses, dateRange, dateField, limoff);
        List<RequisitionView> resultViews = results.getResults().stream().map(RequisitionView::new).collect(Collectors.toList());
        return ListViewResponse.of(resultViews, results.getTotal(), results.getLimOff());
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

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void saveRequisition(@PathVariable int id, @RequestBody RequisitionVersionView newVersionView) {
        Requisition requisition = requisitionService.getRequisitionById(id);
        newVersionView.setCreatedBy(getSubjectEmployeeView());
        newVersionView.setId(0);
        requisition.addVersion(LocalDateTime.now(), newVersionView.toRequisitionVersion());
        requisitionService.saveRequisition(requisition);
    }

    @RequestMapping("/{id}/undoReject")
    public void undoRejection(@PathVariable int id) {
        // TODO: this is not setting the correct created by employee.
        Requisition requisition = requisitionService.getRequisitionById(id);
        requisitionService.undoRejection(requisition);
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
