package gov.nysenate.ess.supply.reconcilation.dao;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.supply.reconcilation.model.Inventory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;


@Repository
public class OracleInventoryDao extends SqlBaseDao {

    /**
     * Query the current inventory of all supply items at the given location from oracle.
     *
     * @return {@link Inventory} for the given location.
     */
    public Inventory forLocation(LocationId locId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("cdlocat", locId.getCode())
                .addValue("cdloctype", locId.getType().getCode() + "");
        String sql = OracleItemInventoryQuery.REC_ORDER_QUERY.getSql(schemaMap());
        InventoryRowHandler handler = new InventoryRowHandler();
        remoteNamedJdbc.query(sql, params, handler);
        return handler.results();
    }
}
