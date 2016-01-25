package gov.nysenate.ess.supply.sfms.dao;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.service.SupplyItemService;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.sfms.SfmsOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class EssSfmsOrderDao extends SqlBaseDao implements SfmsOrderDao {

    @Autowired
    private SupplyItemService itemService;

    @Override
    public int getNuIssue() {
        return 1;
    }

    @Override
    public List<SfmsOrder> getOrders(String locCode, String locType, String issueEmpName, Range<LocalDate> dateRange, LimitOffset limOff) {
        locCode = formatForOracle(locCode);
        locType = formatForOracle(locType);
        issueEmpName = formatForOracle(issueEmpName);
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nuIssue", getNuIssue())
                .addValue("locCode", locCode)
                .addValue("locType", locType)
                .addValue("issueEmpName", issueEmpName)
                .addValue("startDate", toDate(DateUtils.startOfDateRange(dateRange)))
                .addValue("endDate", toDate(DateUtils.endOfDateRange(dateRange)));
        String sql = EssSfmsOrderQuery.GET_ORDERS.getSql(schemaMap(), limOff);
        SfmsOrderHandler handler = new SfmsOrderHandler(itemService);
        remoteNamedJdbc.query(sql, params, handler);
        return handler.getSfmsOrders();
    }

    @Override
    public void saveOrder(Order order) {
        Map<String, Object> baseParams = new HashMap<>();
        baseParams.put("nuIssue", getNuIssue());
        baseParams.put("issueDate", toDate(order.getCompletedDateTime()));
        baseParams.put("locType", String.valueOf(order.getLocation().getType().getCode()));
        baseParams.put("locCode", order.getLocation().getCode());
        baseParams.put("issueEmpName", order.getIssuingEmployee().getLastName());
        baseParams.put("completingUserUid", order.getIssuingEmployee().getUid());

        List<MapSqlParameterSource> batchParams = new ArrayList<>();
        for (LineItem lineItem: order.getItems()) {
            MapSqlParameterSource params = new MapSqlParameterSource(baseParams)
                    .addValue("itemId", "")
                    .addValue("quantity", "")
                    .addValue("unit", "");
        }

    }

    private String formatForOracle(String param) {
        return param != null && param.equals("all") ? "%" : param;
    }
}
