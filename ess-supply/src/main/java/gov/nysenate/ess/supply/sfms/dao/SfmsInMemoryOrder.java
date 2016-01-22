package gov.nysenate.ess.supply.sfms.dao;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.sfms.SfmsOrder;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Primary
@Profile("test")
@Repository
public class SfmsInMemoryOrder implements SfmsOrderDao {

    private Map<Integer, SfmsOrder> orderDB = new TreeMap<>();

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
        return locCode.equals("all") || order.getLocCode().equals(locCode);
    }

    private boolean matchesLocType(String locType, SfmsOrder order) {
        return locType.equals("all") || order.getLocType().equals(locType);
    }

    private boolean matchesIssuer(String issueEmpName, SfmsOrder order) {
        // TODO: should verify issuerEmpId can be converted to int.
        return issueEmpName.equals("all") || order.getIssuedBy().equals(issueEmpName);
    }

    @Override
    public void saveOrder(Order order) {
        orderDB.put(order.getId(), SfmsOrder.fromOrder(order));
    }
}
