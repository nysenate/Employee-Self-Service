package gov.nysenate.ess.supply.order.dao.sfms;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.order.Order;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class EssSfmsOrderDao implements SfmsOrderDao {

    @Override
    public int getNuIssue() {
        return 0;
    }

    @Override
    public List<Order> getOrders(Range<LocalDate> dateRange, LimitOffset limOff) {
        return null;
    }

    @Override
    public List<Order> getOrders(String locCode, String locType, String issuerEmpId, Range<LocalDate> dateRange, LimitOffset limOff) {
        return null;
    }

    @Override
    public void saveOrder(Order order) {

    }

    @Override
    public void updateOrder(Order order) {

    }
}
