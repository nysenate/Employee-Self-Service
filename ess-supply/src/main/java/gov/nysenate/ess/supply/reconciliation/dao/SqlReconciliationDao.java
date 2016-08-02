package gov.nysenate.ess.supply.reconciliation.dao;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.supply.reconciliation.Reconciliation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Chenguang He on 7/28/2016.
 */
@Repository
public class SqlReconciliationDao extends SqlBaseDao implements ReconciliationDao {
    private Logger logger = LoggerFactory.getLogger(SqlReconciliationDao.class);

    @Override
    public ImmutableList<Reconciliation> getReconciliation() {
        String sql = SqlReconciliationDao.SqlReconciliationQuery.GET_Reconciliation_BY_ItemCategory.getSql(schemaMap());
        ReconciliationRowMapper rowMapper = new ReconciliationRowMapper();
        List<Reconciliation> reconciliationList = localNamedJdbc.query(sql, rowMapper);
        ImmutableList<Reconciliation> result = ImmutableList.copyOf(reconciliationList);
        return result;
    }

    private enum SqlReconciliationQuery implements BasicSqlQuery {
        GET_Reconciliation_BY_ItemCategory(
                "SELECT * from ${supplySchema}.reconciliation_category_groups"
        );
        private String sql;

        SqlReconciliationQuery(String sql) {
            this.sql = sql;
        }

        @Override
        public String getSql() {
            return sql;
        }

        @Override
        public DbVendor getVendor() {
            return DbVendor.POSTGRES;
        }

    }

    private class ReconciliationRowMapper extends BaseRowMapper<Reconciliation> {

        @Override
        public Reconciliation mapRow(ResultSet resultSet, int i) throws SQLException {
            return new Reconciliation.Builder().withItemCatagory(resultSet.getString("item_category")).withPage(resultSet.getInt("page")).build();
        }
    }
}
