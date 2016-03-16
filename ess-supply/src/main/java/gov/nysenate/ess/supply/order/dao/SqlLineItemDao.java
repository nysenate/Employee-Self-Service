package gov.nysenate.ess.supply.order.dao;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.order.OrderVersion;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class SqlLineItemDao extends SqlBaseDao implements LineItemDao {

    @Override
    public void insertVersionLineItems(OrderVersion version, int versionId) {
        List<SqlParameterSource> paramList = new ArrayList<>();
        for (LineItem lineItem: version.getLineItems()) {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("versionId", versionId)
                    .addValue("itemId", lineItem.getItem().getId())
                    .addValue("quantity", lineItem.getQuantity());
            paramList.add(params);
        }
        String sql = SqlLineItemQuery.INSERT_LINE_ITEM.getSql(schemaMap());
        SqlParameterSource[] batchParams = new SqlParameterSource[paramList.size()];
        batchParams = paramList.toArray(batchParams);
        localNamedJdbc.batchUpdate(sql, batchParams);
    }

    private enum SqlLineItemQuery implements BasicSqlQuery {

        INSERT_LINE_ITEM(
                "INSERT INTO ${supplySchema}.line_item(version_id, item_id, quantity) \n" +
                "VALUES (:versionId, :itemId, :quantity)"
        );

        SqlLineItemQuery(String sql) {
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
}
