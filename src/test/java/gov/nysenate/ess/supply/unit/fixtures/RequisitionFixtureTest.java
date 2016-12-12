package gov.nysenate.ess.supply.unit.fixtures;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.supply.allowance.ItemVisibility;
import gov.nysenate.ess.supply.item.model.Category;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.model.SupplyItem;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@org.junit.experimental.categories.Category(UnitTest.class)
public class RequisitionFixtureTest {

    public static Requisition baseRequisition() {
         return new Requisition.Builder()
                .withRequisitionId(1)
                .withRevisionId(1)
                .withCustomer(createEmployeeWithId(1))
                .withDestination(createStubLocation())
                .withLineItems(new HashSet<>())
                .withStatus(RequisitionStatus.PENDING)
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
                .withLineItems(createStubLineItem())
                .withStatus(RequisitionStatus.PENDING)
                .withIssuer(createEmployeeWithId(2))
                .withModifiedBy(createEmployeeWithId(1))
                .withModifiedDateTime(LocalDateTime.of(2016, 1, 1, 1, 1))
                .withOrderedDateTime(LocalDateTime.of(2016, 1, 1, 1, 1))
                .build();
    }

    public static Set<LineItem> createStubLineItem() {
//        SupplyItem stubItem = new SupplyItem(2, "AA", "", "", new Category(""), 1, 1, 1, ItemVisibility.VISIBLE, true);
        Set<LineItem> stubLineItems = new HashSet<>();
//        stubLineItems.add(new LineItem(stubItem, 1));
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
