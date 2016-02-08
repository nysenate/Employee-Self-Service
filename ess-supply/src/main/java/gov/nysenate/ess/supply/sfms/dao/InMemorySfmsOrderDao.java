package gov.nysenate.ess.supply.sfms.dao;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.sfms.SfmsLineItem;
import gov.nysenate.ess.supply.sfms.SfmsOrder;
import gov.nysenate.ess.supply.sfms.SfmsOrderId;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Primary
@Profile("test")
@Repository
public class InMemorySfmsOrderDao implements SfmsOrderDao {

    /** Map of sfms order id's to sfms orders */
    private Map<SfmsOrderId, SfmsOrder> orderDB;

    public InMemorySfmsOrderDao() {
        reset();
    }

    public void reset() {
        orderDB = new HashMap<>();
    }

    public int getNuIssue() {
        return 0;
    }

    @Override
    public List<SfmsOrder> getOrders(String locCode, String locType, String issueEmpName, Range<LocalDate> dateRange, LimitOffset limOff) {
        List<SfmsOrder> filteredOrders = new ArrayList<>();
        for (SfmsOrder order : orderDB.values()) {
            if (matchesLocCode(locCode, order) && matchesLocType(locType, order) && matchesIssuer(issueEmpName, order)) {
                filteredOrders.add(order);
            }
        }
        return filteredOrders;
    }

    private boolean matchesLocCode(String locCode, SfmsOrder order) {
        return locCode.equals("all") || order.getOrderId().getToLocationCode().equals(locCode);
    }

    private boolean matchesLocType(String locType, SfmsOrder order) {
        return locType.equals("all") || order.getOrderId().getToLocationType().equals(locType);
    }

    private boolean matchesIssuer(String issueEmpName, SfmsOrder order) {
        // TODO: should verify issuerEmpId can be converted to int.
        return issueEmpName.equals("all") || order.getIssuedBy().equals(issueEmpName);
    }

    @Override
    public SfmsOrder getOrderById(SfmsOrderId orderId) {
        return orderDB.get(orderId);
    }

    @Override
    public void saveOrder(Order order) {
        SfmsOrderId id = new SfmsOrderId(1, order.getOrderDateTime().toLocalDate(), order.getLocation().getCode(),
                                         String.valueOf(order.getLocation().getType().getCode()));
        SfmsOrder sfmsOrder = new SfmsOrder(id);
        sfmsOrder.setFromLocationCode("LC100S");
        sfmsOrder.setFromLocationType("P");
        sfmsOrder.setUpdateDateTime(LocalDateTime.now());
        sfmsOrder.setOriginDateTime(LocalDateTime.now());
        sfmsOrder.setUpdateEmpUid(order.getCustomer().getUid());
        sfmsOrder.setOriginalEmpUid(order.getCustomer().getUid());
        sfmsOrder.setIssuedBy(order.getIssuingEmployee().map(Employee::getLastName).orElse(""));
        // Not setting responsibility center head
        for (LineItem lineItem : order.getLineItems()) {
            SfmsLineItem sfmsLineItem = new SfmsLineItem();
            sfmsLineItem.setItemId(lineItem.getItem().getId());
            sfmsLineItem.setQuantity(lineItem.getQuantity());
            sfmsLineItem.setUnit(lineItem.getItem().getUnit());
            sfmsOrder.addItem(sfmsLineItem);
        }
        orderDB.put(sfmsOrder.getOrderId(), sfmsOrder);
    }
}
