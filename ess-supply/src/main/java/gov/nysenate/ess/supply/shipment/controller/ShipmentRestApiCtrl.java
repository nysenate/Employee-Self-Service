package gov.nysenate.ess.supply.shipment.controller;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.auth.SenatePerson;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.shipment.Shipment;
import gov.nysenate.ess.supply.shipment.ShipmentService;
import gov.nysenate.ess.supply.shipment.ShipmentStatus;
import gov.nysenate.ess.supply.shipment.view.ShipmentView;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supply/shipments")
public class ShipmentRestApiCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(ShipmentRestApiCtrl.class);

    @Autowired private ShipmentService shipmentService;
    @Autowired private EmployeeInfoService employeeService;

    @RequestMapping("/{id}")
    public BaseResponse getShipmentById(@PathVariable int id) {
        Shipment shipment = shipmentService.getShipmentById(id);
        return new ViewObjectResponse<>(new ShipmentView(shipment));
    }

    @RequestMapping("")
    public BaseResponse searchShipments(@RequestParam(defaultValue = "all", required = false) String issuerId,
                                        @RequestParam(required = false) String[] status,
                                        @RequestParam(required = false) String from,
                                        @RequestParam(required = false) String to,
                                        WebRequest webRequest) {
        LocalDateTime fromDateTime = from == null ? LocalDateTime.now().minusMonths(1) : parseISODateTime(from, "from");
        LocalDateTime toDateTime = to == null ? LocalDateTime.now() : parseISODateTime(to, "to");
        EnumSet<ShipmentStatus> statuses = status == null ? EnumSet.allOf(ShipmentStatus.class) : getEnumSetFromStringArray(status);

        LimitOffset limoff = getLimitOffset(webRequest, 25);
        Range<LocalDateTime> dateRange = getClosedRange(fromDateTime, toDateTime, "from", "to");
        PaginatedList<Shipment> results = shipmentService.searchShipments(issuerId, statuses, dateRange, limoff);
        List<ShipmentView> shipmentViews = results.getResults().stream().map(ShipmentView::new).collect(Collectors.toList());
        return ListViewResponse.of(shipmentViews, results.getTotal(), results.getLimOff());
    }

    private EnumSet<ShipmentStatus> getEnumSetFromStringArray(String[] status) {
        List<ShipmentStatus> statusList = new ArrayList<>();
        for (String s : status) {
            statusList.add(ShipmentStatus.valueOf(s));
        }
        return EnumSet.copyOf(statusList);
    }

    @RequestMapping(value = "/{id}/process", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void processShipment(@PathVariable int id, @RequestBody int issuerId) {
        Shipment shipment = shipmentService.getShipmentById(id);
        Employee issuer = employeeService.getEmployee(issuerId);
        Employee modifiedBy = employeeService.getEmployee(getSubjectEmployeeId());
        shipmentService.processShipment(shipment, issuer, modifiedBy);
    }

    @RequestMapping(value = "{id}/complete", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void completeShipment(@PathVariable int id) {
        Shipment shipment = shipmentService.getShipmentById(id);
        Employee modifiedBy = employeeService.getEmployee(getSubjectEmployeeId());
        shipmentService.completeShipment(shipment, modifiedBy);
    }

    @RequestMapping(value = "{id}/undo_completion", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void undoCompletioni(@PathVariable int id) {
        Shipment shipment = shipmentService.getShipmentById(id);
        Employee modifiedBy = employeeService.getEmployee(getSubjectEmployeeId());
        shipmentService.undoCompletion(shipment, modifiedBy);
    }

    @RequestMapping(value = "{id}/approve", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void submitToSfms(@PathVariable int id) {
        if(!getSubject().isPermitted("supply:shipment:approve")) {
            throw new UnauthorizedException();
        }
        Shipment shipment = shipmentService.getShipmentById(id);
        Employee modifiedBy = employeeService.getEmployee(getSubjectEmployeeId());
        shipmentService.approveShipment(shipment, modifiedBy);
    }

    @RequestMapping(value = "{id}/cancel", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void cancelShipment(@PathVariable int id) {
        Shipment shipment = shipmentService.getShipmentById(id);
        Employee modifiedBy = employeeService.getEmployee(getSubjectEmployeeId());
        shipmentService.cancelShipment(shipment, modifiedBy);
    }

    @RequestMapping(value = "{id}/issuer/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateIssuer(@PathVariable int id, @RequestBody int issuerId) {
        Shipment shipment = shipmentService.getShipmentById(id);
        Employee issuer = employeeService.getEmployee(issuerId);
        Employee modifiedBy = employeeService.getEmployee(getSubjectEmployeeId());
        shipmentService.updateIssuingEmployee(shipment, issuer, modifiedBy);
    }

    private int getSubjectEmployeeId() {
        SenatePerson person = (SenatePerson) getSubject().getPrincipals().getPrimaryPrincipal();
        return person.getEmployeeId();
    }
}
