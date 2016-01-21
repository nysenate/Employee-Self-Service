package gov.nysenate.ess.supply.order.dao.sfms;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.dao.sfms.SfmsOrderDao;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Primary
@Profile("test")
@Repository
public class SfmsInMemoryOrder implements SfmsOrderDao {

    private Map<Integer, Order> orderDB = new TreeMap<>();

    public SfmsInMemoryOrder() {
        reset();
    }

    public void reset() {
        orderDB = new TreeMap<>();
    }

    public int getNuIssue() {
        return 0;
    }

    @Override
    public List<Order> getOrders(String locCode, String locType, String issuerEmpId, Range<LocalDate> dateRange, LimitOffset limOff) {
        List<Order> filteredOrders = new ArrayList<>();
        for (Order order : orderDB.values()) {
            if (matchesLocCode(locCode, order) && matchesLocType(locType, order) && matchesIssuer(issuerEmpId, order)) {
                filteredOrders.add(order);
            }
        }
        return filteredOrders;
    }

    private boolean matchesLocCode(String locCode, Order order) {
        return locCode.equals("all") || order.getLocation().getCode().equals(locCode);
    }

    private boolean matchesLocType(String locType, Order order) {
        return locType.equals("all") || String.valueOf(order.getLocation().getType().getCode()).equals(locType);
    }

    private boolean matchesIssuer(String issuerEmpId, Order order) {
        // TODO: should verify issuerEmpId can be converted to int.
        return issuerEmpId.equals("all") || order.getIssuingEmployee().getEmployeeId() == Integer.valueOf(issuerEmpId);
    }

    @Override
    public void saveOrder(Order order) {
        orderDB.put(order.getId(), order);
    }

    @Override
    public void updateOrder(Order order) {

    }
}
