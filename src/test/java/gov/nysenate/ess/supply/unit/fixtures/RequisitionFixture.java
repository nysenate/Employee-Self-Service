package gov.nysenate.ess.supply.unit.fixtures;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.requisition.model.DeliveryMethod;
import gov.nysenate.ess.supply.requisition.model.PendingState;
import gov.nysenate.ess.supply.requisition.model.Requisition;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class RequisitionFixture {

    public static Requisition baseRequisition() {
        return new Requisition.Builder()
                .withRequisitionId(1)
                .withRevisionId(1)
                .withCustomer(createEmployeeWithId(1))
                .withDestination(createStubLocation())
                .withDeliveryMethod(DeliveryMethod.DELIVERY)
                .withLineItems(new HashSet<>())
                .withState(new PendingState())
                .withIssuer(createEmployeeWithId(2))
                .withModifiedBy(createEmployeeWithId(1))
                .withModifiedDateTime(LocalDateTime.of(2016, 1, 1, 1, 1))
                .withOrderedDateTime(LocalDateTime.of(2016, 1, 1, 1, 1))
                .build();
    }

    public static Requisition getPendingRequisition() {
        return new Requisition.Builder()
                .withRequisitionId(0)
                .withRevisionId(0)
                .withCustomer(createEmployeeWithId(1))
                .withDestination(createStubLocation())
                .withDeliveryMethod(DeliveryMethod.DELIVERY)
                .withLineItems(createStubLineItem())
                .withState(new PendingState())
                .withIssuer(createEmployeeWithId(2))
                .withModifiedBy(createEmployeeWithId(1))
                .withModifiedDateTime(LocalDateTime.of(2016, 1, 1, 1, 1))
                .withOrderedDateTime(LocalDateTime.of(2016, 1, 1, 1, 1))
                .build();
    }

    public static Set<LineItem> createStubLineItem() {
        return new HashSet<>();
    }

    public static Location createStubLocation() {
        return new Location(new LocationId("A42FB", 'W'));
    }

    public static Employee createEmployeeWithId(int id) {
        Employee emp = new Employee();
        emp.setEmployeeId(id);
        return emp;
    }
}
