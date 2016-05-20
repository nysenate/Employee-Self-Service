package gov.nysenate.ess.supply.unit.requisition;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.supply.item.Category;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;
import gov.nysenate.ess.supply.requisition.RequisitionVersion;

import java.util.HashSet;
import java.util.Set;

public class RequisitionFixture {

    public static RequisitionVersion getPendingVersion() {
        return new RequisitionVersion.Builder()
                .withId(1)
                .withCustomer(createEmployeeWithId(1))
                .withDestination(createStubLocation())
                .withStatus(RequisitionStatus.PENDING)
                .withLineItems(createStubLineItem())
                .withIssuer(createEmployeeWithId(2))
                .withModifiedBy(createEmployeeWithId(1))
                .withNote("A note")
                .build();
    }

    public static RequisitionVersion getProcessingVersion() {
        return new RequisitionVersion.Builder()
                .withId(2)
                .withCustomer(createEmployeeWithId(1))
                .withDestination(createStubLocation())
                .withStatus(RequisitionStatus.PROCESSING)
                .withLineItems(createStubLineItem())
                .withIssuer(createEmployeeWithId(2))
                .withModifiedBy(createEmployeeWithId(3))
                .build();
    }

    public static RequisitionVersion getRejectedVersion() {
        return new RequisitionVersion.Builder()
                .withId(3)
                .withCustomer(createEmployeeWithId(1))
                .withDestination(createStubLocation())
                .withStatus(RequisitionStatus.REJECTED)
                .withLineItems(createStubLineItem())
                .withIssuer(createEmployeeWithId(2))
                .withModifiedBy(createEmployeeWithId(4))
                .build();
    }

    public static RequisitionVersion getCompletedVersion() {
        return new RequisitionVersion.Builder()
                .withId(4)
                .withCustomer(createEmployeeWithId(1))
                .withDestination(createStubLocation())
                .withStatus(RequisitionStatus.COMPLETED)
                .withLineItems(createStubLineItem())
                .withIssuer(createEmployeeWithId(2))
                .withModifiedBy(createEmployeeWithId(2))
                .build();
    }

    public static RequisitionVersion getApprovedVersion() {
        return new RequisitionVersion.Builder()
                .withId(5)
                .withCustomer(createEmployeeWithId(1))
                .withDestination(createStubLocation())
                .withStatus(RequisitionStatus.APPROVED)
                .withLineItems(createStubLineItem())
                .withIssuer(createEmployeeWithId(2))
                .withModifiedBy(createEmployeeWithId(2))
                .build();
    }

    public static RequisitionVersion getMinimalPendingVersion() {
        return new RequisitionVersion.Builder()
                .withCustomer(createEmployeeWithId(1))
                .withDestination(createStubLocation())
                .withStatus(RequisitionStatus.PENDING)
                .withLineItems(createStubLineItem())
                .withModifiedBy(createEmployeeWithId(1))
                .build();
    }

    public static Set<LineItem> createStubLineItem() {
        SupplyItem stubItem = new SupplyItem(1, "", "", "", new Category(""), 1, 1, 1);
        Set<LineItem> stubLineItems = new HashSet<>();
        stubLineItems.add(new LineItem(stubItem, 1));
        return stubLineItems;
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
