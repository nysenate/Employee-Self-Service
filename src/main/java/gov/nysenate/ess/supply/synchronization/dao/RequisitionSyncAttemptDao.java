package gov.nysenate.ess.supply.synchronization.dao;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.supply.synchronization.model.RequisitionSyncAttempt;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class RequisitionSyncAttemptDao extends SqlBaseDao {

    public void insertRequisitionSyncAttempt(RequisitionSyncAttempt requisitionSyncAttempt) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("requisition_history_id", requisitionSyncAttempt.getRequisitionHistoryId())
                .addValue("requisition_id", requisitionSyncAttempt.getRequisitionId())
                .addValue("sync_attempts", requisitionSyncAttempt.getSyncAttempts())
                .addValue("attempt_sync_date", requisitionSyncAttempt.getAttemptSyncDate().toString())
                .addValue("was_successful", requisitionSyncAttempt.getWasSuccessful())
                .addValue("error_info", requisitionSyncAttempt.getErrorInfo() != null ? requisitionSyncAttempt.getErrorInfo() : null)
                .addValue("outcome_sync_status", requisitionSyncAttempt.getOutcomeSyncStatus().name())
                .addValue("syncable_line_items", requisitionSyncAttempt.getSyncableLineItems());

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
