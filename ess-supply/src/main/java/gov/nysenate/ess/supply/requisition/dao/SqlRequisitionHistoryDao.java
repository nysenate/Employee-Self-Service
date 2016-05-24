package gov.nysenate.ess.supply.requisition.dao;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class SqlRequisitionHistoryDao extends SqlBaseDao {

    protected void insertRequisitionHistory(int requisitionId, int versionId, LocalDateTime createdDateTime) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("requisitionId", requisitionId)
                .addValue("versionId", versionId)
                .addValue("createdDateTime", toDate(createdDateTime));
        String sql = SqlRequisitionHistoryQuery.INSERT_REQUISITION_HISTORY.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private enum SqlRequisitionHistoryQuery implements BasicSqlQuery {
        INSERT_REQUISITION_HISTORY(
                "INSERT INTO ${supplySchema}.requisition_history(requisition_id, version_id, created_date_time) \n" +
                "VALUES (:requisitionId, :versionId, :createdDateTime)"
        );

        private String sql;

        SqlRequisitionHistoryQuery(String sql) {
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
}
