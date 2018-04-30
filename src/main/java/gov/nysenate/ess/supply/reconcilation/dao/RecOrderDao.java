package gov.nysenate.ess.supply.reconcilation.dao;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.supply.reconcilation.model.RecOrder;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
public class RecOrderDao extends SqlBaseDao {

    /**
     * Get a set of items from a set of item ids.
     */
    public HashSet<RecOrder> getItemsById(Set<String> ids){
        MapSqlParameterSource params = new MapSqlParameterSource("ids", ids);
        String sql = OracleRecItemQuery.GET_REC_ORDER_BY_IDS.getSql(schemaMap());
        HashSet<RecOrder> items = new HashSet<>(remoteNamedJdbc.query(sql, params, new RecItemRowMapper()));
        return items;
    }

    public HashSet<RecOrder> getItemById(int id){
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        String sql = OracleRecItemQuery.GET_REC_ORDER_BY_ID.getSql(schemaMap());
        HashSet<RecOrder> items = new HashSet<>(remoteNamedJdbc.query(sql, params, new RecItemRowMapper()));
        return items;
    }

    public HashSet<RecOrder> getItems(){
        String sql = OracleRecItemQuery.REC_ORDER_QUERY.getSql(schemaMap());
        HashSet<RecOrder> items = new HashSet<>(remoteNamedJdbc.query(sql, new RecItemRowMapper()));
        return items;
    }

}
