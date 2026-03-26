package gov.nysenate.ess.supply.synchronization.dao;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.supply.synchronization.model.RequisitionSyncAttempt;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class RequisitionSyncAttemptDao extends SqlBaseDao implements SyncAttemptDao {

    public void insertRequisitionSyncAttempt(RequisitionSyncAttempt requisitionSyncAttempt) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("requisitionId", requisitionSyncAttempt.getRequisitionId())
                .addValue("attemptCount", requisitionSyncAttempt.getAttemptCount())
                .addValue("attemptDateTime", requisitionSyncAttempt.getAttemptDateTime())
                .addValue("wasSuccessful", requisitionSyncAttempt.getWasSuccessful())
                .addValue("errorMsg", requisitionSyncAttempt.getErrorMsg() != null ? requisitionSyncAttempt.getErrorMsg() : null)
                .addValue("outcomeSyncStatus", requisitionSyncAttempt.getOutcomeSyncStatus().name())
                .addValue("syncedItemIds", requisitionSyncAttempt.getSyncedItemIds().toArray(new Integer[0]));
        String sql = SqlReqHistoryQuery.INSERT_REQUISITION_HISTORY.getSql(schemaMap());

        localNamedJdbc.update(sql, params);
    }


    private enum SqlReqHistoryQuery implements BasicSqlQuery {
        INSERT_REQUISITION_HISTORY(
                """
                INSERT INTO ${supplySchema}.requisition_sync_attempt
                    (
                        requisition_id,
                        attempt_count,
                        attempt_date_time,
                        was_successful,
                        outcome_sync_status,
                        error_msg,
                        synced_item_ids
                    )
                VALUES (
                    :requisitionId,
                    :attemptCount,
                    :attemptDateTime,
                    :wasSuccessful,
                    :outcomeSyncStatus,
                    :errorMsg,
                    :syncedItemIds::int[]
                )
                """
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
