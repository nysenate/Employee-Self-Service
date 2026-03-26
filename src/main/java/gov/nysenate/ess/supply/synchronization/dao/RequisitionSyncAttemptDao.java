package gov.nysenate.ess.supply.synchronization.dao;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.supply.synchronization.model.RequisitionSyncAttempt;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Primary
public class RequisitionSyncAttemptDao extends SqlBaseDao implements SyncAttemptDao {

    public void insertRequisitionSyncAttempt(RequisitionSyncAttempt requisitionSyncAttempt) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("requisitionHistoryId", requisitionSyncAttempt.getRequisitionHistoryId())
                .addValue("requisitionId", requisitionSyncAttempt.getRequisitionId())
                .addValue("syncAttempts", requisitionSyncAttempt.getSyncAttempts())
                .addValue("attemptSyncDate", requisitionSyncAttempt.getAttemptSyncDate())
                .addValue("wasSuccessful", requisitionSyncAttempt.getWasSuccessful())
                .addValue("errorInfo", requisitionSyncAttempt.getErrorInfo() != null ? requisitionSyncAttempt.getErrorInfo() : null)
                .addValue("outcomeSyncStatus", requisitionSyncAttempt.getOutcomeSyncStatus().name())
                .addValue("syncableLineItems", requisitionSyncAttempt.getSyncableLineItems().toArray(new Integer[0]));
        String sql = SqlReqHistoryQuery.INSERT_REQUISITION_HISTORY.getSql(schemaMap());

        localNamedJdbc.update(sql, params);
    }


    private enum SqlReqHistoryQuery implements BasicSqlQuery {
        INSERT_REQUISITION_HISTORY(
                "INSERT INTO ${supplySchema}.requisition_sync_attempt (requisition_id, sync_attempts, attempt_sync_date, was_successful, outcome_sync_status, error_info, syncable_line_items) VALUES (:requisitionId, :syncAttempts, :attemptSyncDate, :wasSuccessful, :outcomeSyncStatus, :errorInfo, :syncableLineItems::int[])"
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
