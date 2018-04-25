package gov.nysenate.ess.supply.reconcilation.dao;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.supply.reconcilation.model.RecOrder;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.HashSet;
import java.util.Set;

public class RecOrderDao extends SqlBaseDao {

    /**
     * Get a set of items from a set of item ids.
     */
    public Set<RecOrder> getItemsById(Set<Integer> ids){
        MapSqlParameterSource params = new MapSqlParameterSource("ids", ids);
        String sql = OracleRecItemQuery.GET_REC_ORDER_BY_IDS.getSql(schemaMap());
        Set<RecOrder> items = new HashSet<>(remoteNamedJdbc.query(sql, params, new RecItemRowMapper()));
        return items;

    }

}
