package gov.nysenate.ess.supply.synchronization.dao;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.supply.synchronization.model.RequisitionHistory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class RequisitionHistoryDao extends SqlBaseDao {

    protected void insertRequisitionHistory(RequisitionHistory requisitionHistory) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("requisition_history_id", requisitionHistory.getRequisition_history_id())
                .addValue("requisition_id", requisitionHistory.getRequisition_id())
                .addValue("sync_attempts", requisitionHistory.getSync_attempts())
                .addValue("attempt_sync_date", requisitionHistory.getAttempt_sync_date().toString())
                .addValue("was_successful", requisitionHistory.getWas_successful())
                .addValue("error_info", requisitionHistory.getError_info() != null ? requisitionHistory.getError_info() : null)
                .addValue("outcome_sync_status", requisitionHistory.getOutcome_sync_status().name())
                .addValue("syncable_line_items", requisitionHistory.getSyncable_line_items());

        String sql = SqlReqHistoryQuery.INSERT_REQUISITION_HISTORY.getSql(schemaMap());

        localNamedJdbc.update(sql, params);
    }

    private enum SqlReqHistoryQuery implements BasicSqlQuery {
        INSERT_REQUISITION_HISTORY(
                "INSERT INTO ${supply.schema} \n" +
                        "VALUES (:requisition_history_id, :requisition_id, " +
                        ":sync_attempts, :attempt_sync_date, :was_successful, " +
                        ":error_info, :outcome_sync_status, :syncable_line_items)"
        );

        private final String sql;

        SqlReqHistoryQuery(String sql) {
            this.sql = sql;
        }

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
