package gov.nysenate.ess.supply.requisition.dao;


import com.google.common.collect.Range;
import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.item.service.SupplyItemService;
import gov.nysenate.ess.supply.requisition.Requisition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class SqlLineItemDao extends SqlBaseDao {

    @Autowired private SupplyItemService itemService;

    /**
     * Does a batch insert of all line items for a requisition.
     * @param requisition
     */
    protected void insertRequisitionLineItems(Requisition requisition) {
        List<SqlParameterSource> paramList = new ArrayList<>();
        for (LineItem lineItem: requisition.getLineItems()) {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("revisionId", requisition.getRevisionId())
                    .addValue("itemId", lineItem.getItem().getId())
                    .addValue("quantity", lineItem.getQuantity());
            paramList.add(params);
        }
        String sql = SqlReqLineItemQuery.INSERT_LINE_ITEM.getSql(schemaMap());
        SqlParameterSource[] batchParams = new SqlParameterSource[paramList.size()];
        batchParams = paramList.toArray(batchParams);
        localNamedJdbc.batchUpdate(sql, batchParams);
    }

    protected Set<LineItem> getLineItems(int revisionId) {
        MapSqlParameterSource params = new MapSqlParameterSource("revisionId", revisionId);
        String sql = SqlReqLineItemQuery.GET_LINE_ITEMS.getSql(schemaMap());
        ReqLineItemHandler handler = new ReqLineItemHandler(itemService);
        localNamedJdbc.query(sql, params, handler);
        return handler.getLineItems();
    }

    public Set<LineItem> getAggregateLineItems(LocationId locationId, Range<LocalDateTime> dateRange) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("locationId", locationId.toString())
                .addValue("from", toDate(DateUtils.startOfDateTimeRange(dateRange)))
                .addValue("to", toDate(DateUtils.endOfDateTimeRange(dateRange)));
        String sql = SqlReqLineItemQuery.LOCATION_AGGREGATE_LINE_ITEMS.getSql(schemaMap());
        ReqLineItemHandler handler = new ReqLineItemHandler(itemService);
        localNamedJdbc.query(sql, params, handler);
        // TODO not going to work, need to sum all items
        return handler.getLineItems();
    }

    private enum SqlReqLineItemQuery implements BasicSqlQuery {

        INSERT_LINE_ITEM(
                "INSERT INTO ${supplySchema}.line_item(revision_id, item_id, quantity) \n" +
                "VALUES (:revisionId, :itemId, :quantity)"
        ),
        GET_LINE_ITEMS(
                "SELECT item_id, quantity \n" +
                "FROM ${supplySchema}.line_item \n" +
                "WHERE revision_id = :revisionId"
        ),
        LOCATION_AGGREGATE_LINE_ITEMS(
                "SELECT item_id, quantity FROM ${supplySchema}.line_item i \n" +
                "INNER JOIN ${supplySchema}.requisition_content c ON c.revision_id = i.revision_id \n" +
                "INNER JOIN ${supplySchema}.requisition r ON r.current_revision_id = c.revision_id \n" +
                "WHERE c.destination = :locationId \n" +
                "AND r.ordered_date_time BETWEEN :from AND :to"
        );

        SqlReqLineItemQuery(String sql) {
            this.sql = sql;
        }

        private String sql;

        @Override
        public String getSql() {
            return this.sql;
        }

        @Override
        public DbVendor getVendor() {
            return DbVendor.POSTGRES;
        }
    }

    /**
     * Return a set of line items sorted alphabetically by their descriptions.
     *
     * Cant sort in original select statement since description information is
     * not contained in local database.
     */
    private class ReqLineItemHandler extends BaseHandler {

        private SupplyItemService itemService;
        private Set<LineItem> lineItems;
        private Comparator alphabeticalItemDesc = new Comparator<LineItem>() {
            @Override
            public int compare(LineItem o1, LineItem o2) {
                return o1.getItem().getDescription().compareTo(o2.getItem().getDescription());
            }
        };

        ReqLineItemHandler(SupplyItemService itemService) {
            this.itemService = itemService;
            lineItems = new TreeSet<>(alphabeticalItemDesc);
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            SupplyItem item = itemService.getItemById(rs.getInt("item_id"));
            int quantity = rs.getInt("quantity");
            lineItems.add(new LineItem(item, quantity));
        }

        Set<LineItem> getLineItems() {
            return lineItems;
        }
    }
}
