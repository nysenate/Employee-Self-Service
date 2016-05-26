package gov.nysenate.ess.supply.requisition.controller;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.unit.LocationService;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.view.LineItemView;
import gov.nysenate.ess.supply.order.view.SubmitOrderView;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionVersion;
import gov.nysenate.ess.supply.requisition.service.RequisitionService;
import gov.nysenate.ess.supply.requisition.view.RequisitionView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supply/requisitions")
public class RequisitionRestApiCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(RequisitionRestApiCtrl.class);

    @Autowired private RequisitionService requisitionService;
    @Autowired private EmployeeInfoService employeeService;
    @Autowired private LocationService locationService;

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void submitRequisition(@RequestBody SubmitOrderView submitRequisitionView) {
        // TODO: move into factory
        Set<LineItem> lineItems = new HashSet<>();
        for (LineItemView lineItemView : submitRequisitionView.getLineItems()) {
            lineItems.add(lineItemView.toLineItem());
        }
        Employee customer = employeeService.getEmployee(submitRequisitionView.getCustomerId());
        Location destination = locationService.getLocation(new LocationId(submitRequisitionView.getDestinationId()));
        RequisitionVersion version = new RequisitionVersion.Builder().withCustomer(customer)
                                                                     .withDestination(destination)
                                                                     .withLineItems(lineItems)
                                                                     .build();
        Requisition requisition = new Requisition(LocalDateTime.now(), version);
        requisitionService.saveRequisition(requisition);
    }

    @RequestMapping("/{id}")
    public BaseResponse getRequisitionById(@PathVariable int id) {
        Requisition requisition = requisitionService.getRequisitionById(id);
        return new ViewObjectResponse<>(new RequisitionView(requisition));
    }
}
