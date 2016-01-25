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

import java.sql.Date;
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
        return 2;
    }

    @Override
    public List<SfmsOrder> getOrders(String locCode, String locType, String issueEmpName, Range<LocalDate> dateRange, LimitOffset limOff) {
        locCode = formatForOracle(locCode);
        locType = formatForOracle(locType);
        issueEmpName = formatForOracle(issueEmpName);
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("locCode", locCode)
                .addValue("locType", locType)
                .addValue("issueEmpName", issueEmpName)
                .addValue("startDate", toDate(DateUtils.startOfDateRange(dateRange)))
                .addValue("endDate", getEndDate(dateRange));
        String sql = EssSfmsOrderQuery.GET_ORDERS.getSql(schemaMap(), limOff);
        SfmsOrderHandler handler = new SfmsOrderHandler(itemService);
        remoteNamedJdbc.query(sql, params, handler);
        return handler.getSfmsOrders();
    }

    /**
     * @param dateRange
     * @return The last day to include from a date range. Must add an day to this value to make inclusive in oracle query.
     */
    private Date getEndDate(Range<LocalDate> dateRange) {
        // Must add a day here, otherwise oracle interprets it as the start of the day and returns no results.
        return toDate(DateUtils.endOfDateRange(dateRange).plusDays(1));
    }

    @Override
    public void saveOrder(Order order) {
        Map<String, Object> baseParams = new HashMap<>();
        baseParams.put("nuIssue", getNuIssue());
        baseParams.put("issueDate", toDate(order.getCompletedDateTime()));
        baseParams.put("locType", String.valueOf(order.getLocation().getType().getCode()));
        baseParams.put("locCode", order.getLocation().getCode());
        baseParams.put("issueEmpName", order.getIssuingEmployee().getLastName().toUpperCase());
        baseParams.put("completingUserUid", order.getIssuingEmployee().getUid().toUpperCase());

        List<MapSqlParameterSource> batchParams = new ArrayList<>();
        for (LineItem lineItem: order.getItems()) {
            MapSqlParameterSource params = new MapSqlParameterSource(baseParams)
                    .addValue("itemId", lineItem.getItem().getId())
                    .addValue("quantity", lineItem.getQuantity())
                    .addValue("unit", lineItem.getItem().getUnit());
            batchParams.add(params);
        }
        String sql = EssSfmsOrderQuery.INSERT_ORDER.getSql(schemaMap());
        remoteNamedJdbc.batchUpdate(sql, toArray(batchParams));
    }

    private MapSqlParameterSource[] toArray(List<MapSqlParameterSource> batchParams) {
        return batchParams.toArray(new MapSqlParameterSource[batchParams.size()]);
    }

    private String formatForOracle(String param) {
        return param != null && param.equals("all") ? "%" : param;
    }
}
