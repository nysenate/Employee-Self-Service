package gov.nysenate.ess.supply.synchronization.dao;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.supply.requisition.model.Requisition;
import gov.nysenate.ess.supply.requisition.model.SyncStatus;
import gov.nysenate.ess.supply.synchronization.model.RequisitionSyncAttempt;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
@Primary
public class RequisitionSyncAttemptDao extends SqlBaseDao implements SyncAttemptDao {

    public void insertRequisitionSyncAttempt(RequisitionSyncAttempt requisitionSyncAttempt) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("requisitionId", requisitionSyncAttempt.getRequisitionId())
                .addValue("revisionId", requisitionSyncAttempt.getRevisionId())
                .addValue("attemptCount", requisitionSyncAttempt.getAttemptCount())
                .addValue("attemptDateTime", requisitionSyncAttempt.getAttemptDateTime())
                .addValue("wasSuccessful", requisitionSyncAttempt.getWasSuccessful())
                .addValue("errorMsg", requisitionSyncAttempt.getErrorMsg() != null ? requisitionSyncAttempt.getErrorMsg() : null)
                .addValue("outcomeSyncStatus", requisitionSyncAttempt.getOutcomeSyncStatus().name())
                .addValue("syncedItemIds", requisitionSyncAttempt.getSyncedItemIds().toArray(new Integer[0]));
        String sql = SqlReqHistoryQuery.INSERT_REQUISITION_HISTORY.getSql(schemaMap());

        localNamedJdbc.update(sql, params);
    }

    public List<RequisitionSyncAttempt> findByRequisitionId(int requisitionId) {
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("requisitionId", requisitionId);
        String sql = SqlReqHistoryQuery.FIND_BY_REQUISITION_ID.getSql(schemaMap());

        List<RequisitionSyncAttempt> results = localNamedJdbc.query(sql, params, (ResultSet rs, int rowNum) -> {
            RequisitionSyncAttempt syncAttempt = new RequisitionSyncAttempt(rs.getInt("requisition_id"), rs.getInt("attempt_count"), rs.getTimestamp("attempt_date_time").toLocalDateTime());
            syncAttempt.setWasSuccessful(rs.getBoolean("was_successful"));
            syncAttempt.setErrorMsg(rs.getString("error_msg"));
            syncAttempt.setOutcomeSyncStatus(SyncStatus.valueOf(rs.getString("outcome_sync_status")));
            syncAttempt.setRevisionId(rs.getInt("revision_id"));

            Array sqlArray = rs.getArray("synced_item_ids");
            Integer[] array = (Integer[]) sqlArray.getArray();
            List<Integer> syncedItemIds = Arrays.asList(array);

            syncAttempt.setSyncedItemIds(syncedItemIds);
            return syncAttempt;
        });

        return results;
    }


    private enum SqlReqHistoryQuery implements BasicSqlQuery {
        INSERT_REQUISITION_HISTORY(
                """
                INSERT INTO ${supplySchema}.requisition_sync_attempt
                    (
                        requisition_id,
                        revision_id,
                        attempt_count,
                        attempt_date_time,
                        was_successful,
                        outcome_sync_status,
                        error_msg,
                        synced_item_ids
                    )
                VALUES (
                    :requisitionId,
                    :revisionId,
                    :attemptCount,
                    :attemptDateTime,
                    :wasSuccessful,
                    :outcomeSyncStatus,
                    :errorMsg,
                    :syncedItemIds::int[]
                )
                """
        ),
        FIND_BY_REQUISITION_ID(
                """
                SELECT * FROM ${supplySchema}.requisition_sync_attempt WHERE requisition_id = :requisitionId
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
